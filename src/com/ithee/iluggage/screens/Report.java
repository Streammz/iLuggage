package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageBrand;
import com.ithee.iluggage.core.database.classes.LuggageColor;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class Report extends SubSceneController {

    private static final String[] MONTH_NAMES = {"Januari", "Februari", "Maart", "April",
        "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November", "December"};
    private static final int[] CHART_STEPS = {5, 10, 25, 50, 100, 250, 500, 1000, 10000};

    @FXML
    private ChoiceBox<LuggageBrand> cbBrand;
    @FXML
    private TextField tfKeywords;
    @FXML
    private ChoiceBox<LuggageKind> cbKind;
    @FXML
    private ChoiceBox<LuggageColor> cbColor;

    @FXML
    private ChoiceBox<String> cbGraphType;
    @FXML
    private ChoiceBox<ReportEntry> cbGraphStart;
    @FXML
    private ChoiceBox<ReportEntry> cbGraphEnd;

    @FXML
    private BarChart chartBar;
    @FXML
    private NumberAxis chartBarY;
    @FXML
    private AreaChart chartArea;
    @FXML
    private NumberAxis chartAreaY;
    @FXML
    private Text chartTitle;

    @FXML
    private BorderPane printView;

    private List<Luggage> results;

    @Override
    public void onCreate() {
        // Hide charts so they can be shown individually later on
        chartBar.setVisible(false);
        chartArea.setVisible(false);

        // Reguliere filters
        cbKind.getItems().add(null);
        app.dbKinds.getValues().forEach((o) -> {
            cbKind.getItems().add(o);
        });
        cbColor.getItems().add(null);
        app.dbColors.getValues().forEach((o) -> {
            cbColor.getItems().add(o);
        });
        cbBrand.getItems().add(null);
        app.dbBrands.getValues().forEach((o) -> {
            cbBrand.getItems().add(o);
        });

        // Datum filters
        cbGraphType.getItems().add("Week");
        cbGraphType.getItems().add("Maand");
        cbGraphType.getItems().add("Jaar");

        cbGraphType.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            // Clear existing items
            cbGraphStart.getItems().clear();
            cbGraphEnd.getItems().clear();
            // Get a calendar instance
            Calendar cal = Calendar.getInstance();

            switch (newVal) {
                case "Week":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    for (int i = 0; i < 53; i++) {
                        ReportWeek week = new ReportWeek(cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
                        cbGraphStart.getItems().add(week);
                        cbGraphEnd.getItems().add(week);

                        cal.add(Calendar.WEEK_OF_YEAR, -1);
                    }
                    break;

                case "Maand":
                    for (int i = 0; i < 24; i++) {
                        ReportMaand maand = new ReportMaand(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
                        cbGraphStart.getItems().add(maand);
                        cbGraphEnd.getItems().add(maand);

                        cal.add(Calendar.MONTH, -1);
                    }
                    break;

                case "Jaar":
                    for (int i = 0; i < 5; i++) {
                        ReportJaar jaar = new ReportJaar(cal.get(Calendar.YEAR));
                        cbGraphStart.getItems().add(jaar);
                        cbGraphEnd.getItems().add(jaar);

                        cal.add(Calendar.YEAR, -1);
                    }
                    break;
            }
            // Set default values on the current week/month/year
            cbGraphStart.setValue(cbGraphStart.getItems().get(0));
            cbGraphEnd.setValue(cbGraphStart.getItems().get(0));

            // Add a listener that checks if the selected end is before the selected start.
            // If this is the case, it sets the end date to the same as the start date
            cbGraphEnd.getSelectionModel().selectedItemProperty().addListener((ov2, oldVal2, newVal2) -> {
                if (cbGraphEnd.getSelectionModel().getSelectedIndex() > cbGraphStart.getSelectionModel().getSelectedIndex()) {
                    cbGraphEnd.setValue(cbGraphEnd.getItems().get(cbGraphStart.getSelectionModel().getSelectedIndex()));
                }
            });
        });

        cbGraphType.setValue("Week");
    }

    public void onSearch(ActionEvent event) {
        loadResults(cbGraphType.getValue(), cbGraphStart.getValue().getStartDate(), cbGraphEnd.getValue().getEndDate());
    }

    public void onPressPrint() {
        PrinterJob pj = PrinterJob.createPrinterJob();
        if (pj.showPrintDialog(stage.getOwner())) {
            printView.setScaleX(0.75);
            printView.setScaleY(0.75);
            printView.setTranslateX(-60);
            printView.setTranslateY(-45);
            printView.setMaxWidth(480);
            printView.setMaxHeight(320);
            if (pj.printPage(this.printView)) {
                pj.endJob();
            }
            printView.setScaleX(1);
            printView.setScaleY(1);
            printView.setTranslateX(0);
            printView.setTranslateY(0);
            printView.setMaxWidth(-1.0);
            printView.setMaxHeight(-1.0);
        }
    }

    private void loadResults(String type, Calendar start, Calendar end) {
        this.results = loadFilteredResults();

        Stream<Luggage> resultStream = results.stream().filter((Luggage lugg) -> {
            Calendar luggCal = Calendar.getInstance();
            luggCal.setTime(lugg.date);

            if (start != null && !start.before(luggCal)) {
                return false;
            }
            if (end != null && !end.after(luggCal)) {
                return false;
            }
            return true;
        });

        XYChart.Series series = new XYChart.Series();
        switch (type) {
            case "Week":
                ReportWeek weekStart = (ReportWeek) cbGraphStart.getValue(),
                 weekEnd = (ReportWeek) cbGraphEnd.getValue();

                if (weekStart == weekEnd) {
                    chartTitle.setText("Rapportage van week " + weekStart.weekNum);
                } else {
                    chartTitle.setText("Rapportage van week " + weekStart.weekNum + " tot en met " + weekEnd.weekNum);
                }

                series.setName("Hoeveelheid aangemaakte bagage");

                // days[0] = maximum amount, days[1-7] = Calendar.MONDAY - Calendar.SUNDAY
                int[] days = new int[8];
                resultStream.forEach((item) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(item.date);
                    days[0] = Math.max(days[0], ++days[cal.get(Calendar.DAY_OF_WEEK)]);
                });

                chartBarY.setUpperBound(boundSize(days[0]));
                series.getData().add(new XYChart.Data("Maandag", days[2]));
                series.getData().add(new XYChart.Data("Dinsdag", days[3]));
                series.getData().add(new XYChart.Data("Woensdag", days[4]));
                series.getData().add(new XYChart.Data("Donderdag", days[5]));
                series.getData().add(new XYChart.Data("Vrijdag", days[6]));
                series.getData().add(new XYChart.Data("Zaterdag", days[7]));
                series.getData().add(new XYChart.Data("Zondag", days[1]));

                chartBar.getData().clear();
                showChart(chartBar);
                chartBar.getData().add(series);

                break;
            case "Maand":
                ReportMaand maandStart = (ReportMaand) cbGraphStart.getValue(),
                 maandEnd = (ReportMaand) cbGraphEnd.getValue();

                if (maandStart == maandEnd) {
                    chartTitle.setText("Rapportage van " + maandStart.monthName + " " + maandStart.jaarNum);
                } else {
                    chartTitle.setText("Rapportage van " + maandStart.monthName + " " + maandStart.jaarNum + " tot en met " + maandEnd.monthName + " " + maandEnd.jaarNum);
                }

                series.setName("Hoeveelheid aangemaakte bagage");

                int[] dates = new int[32];
                resultStream.forEach((item) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(item.date);
                    dates[0] = Math.max(dates[0], ++dates[cal.get(Calendar.DAY_OF_MONTH) + 1]);
                });

                chartAreaY.setUpperBound(boundSize(dates[0]));
                for (int i = 0; i < 31; i++) {
                    series.getData().add(new XYChart.Data(i + 1, dates[i + 1]));
                }

                chartArea.getData().clear();
                showChart(chartArea);
                chartArea.getData().add(series);

                break;
            case "Jaar":
                ReportJaar jaarStart = (ReportJaar) cbGraphStart.getValue(),
                 jaarEnd = (ReportJaar) cbGraphEnd.getValue();

                if (jaarStart == jaarEnd) {
                    chartTitle.setText("Rapportage van " + jaarStart.jaarNum);
                } else {
                    chartTitle.setText("Rapportage van " + jaarStart.jaarNum + " tot en met " + jaarEnd.jaarNum);
                }

                series.setName("Hoeveelheid aangemaakte bagage");
                // days[0] = maximum amount, days[1-7] = Calendar.MONDAY - Calendar.SUNDAY
                int[] months = new int[13];
                resultStream.forEach((item) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(item.date);
                    months[0] = Math.max(months[0], ++months[cal.get(Calendar.MONTH) + 1]);
                });

                chartBarY.setUpperBound(boundSize(months[0]));
                for (int i = 0; i < 12; i++) {
                    series.getData().add(new XYChart.Data(MONTH_NAMES[i], months[i + 1]));
                }
                chartBar.getData().clear();
                showChart(chartBar);
                chartBar.getData().add(series);

                break;
        }
    }

    private List<Luggage> loadFilteredResults() {

        List<String> wheres = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (cbKind.getValue() != null) {
            wheres.add("`Kind` = ?");
            params.add(cbKind.getValue().id);
        }
        if (cbColor.getValue() != null) {
            wheres.add("`Color` = ?");
            params.add(cbColor.getValue().id);
        }
        if (cbBrand.getValue() != null) {
            wheres.add("`Brand` = ?");
            params.add(cbBrand.getValue().id);
        }
        if (tfKeywords.getLength() > 0) {
            String[] keywords = tfKeywords.getText().split("(, ?| )");
            Arrays.stream(keywords).forEach((keyword) -> {
                keyword = keyword.trim();
                wheres.add("`Miscellaneous` LIKE ? OR `FlightCode` LIKE ?");
                for (int i = 0; i < 2; i++) {
                    params.add("%" + keyword + "%");
                }
            });
        }

        String query = "SELECT * FROM `luggage`";
        if (wheres.size() > 0) {
            query += " WHERE ";
            for (int i = 0; i < wheres.size(); i++) {
                query = query + wheres.get(i) + " AND ";
            }
            query = query.substring(0, query.length() - 5);
        }

        return app.db.executeAndReadList(Luggage.class, query, params.toArray());
    }

    public void showChart(Node node) {
        this.chartBar.setVisible(node == chartBar);
        this.chartArea.setVisible(node == chartArea);
    }

    public int boundSize(int size) {
        for (int i = 0; i < CHART_STEPS.length; i++) {
            if (size < CHART_STEPS[i]) {
                return CHART_STEPS[i];
            }
        }
        return CHART_STEPS[CHART_STEPS.length - 1];
    }

    private static interface ReportEntry {

        public Calendar getStartDate();

        public Calendar getEndDate();
    }
    
    private static class ReportType {
        private final int id;
        private final String text;
        
        public ReportType(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static class ReportWeek implements ReportEntry {

        private final int weekNum;
        private final int jaarNum;

        public ReportWeek(int weekNum, int jaarNum) {
            this.weekNum = weekNum;
            this.jaarNum = jaarNum;
        }

        @Override
        public String toString() {
            return "Week " + weekNum + ", " + jaarNum;
        }

        @Override
        public Calendar getStartDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(jaarNum, 1, 1, 0, 0, 0);
            cal.set(Calendar.WEEK_OF_YEAR, weekNum);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            return cal;
        }

        @Override
        public Calendar getEndDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(jaarNum, 1, 1, 23, 59, 59);
            cal.set(Calendar.WEEK_OF_YEAR, weekNum);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            return cal;
        }
    }

    private static class ReportMaand implements ReportEntry {

        private final int monthNum;
        private final int jaarNum;
        private final String monthName;

        public ReportMaand(int monthNum, int jaarNum) {
            this.monthNum = monthNum;
            this.jaarNum = jaarNum;
            this.monthName = getStartDate().getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.getDefault());
        }

        @Override
        public String toString() {
            return this.monthName + ", " + jaarNum;
        }

        @Override
        public final Calendar getStartDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(jaarNum, monthNum, 1, 0, 0, 0);
            return cal;
        }

        @Override
        public Calendar getEndDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(monthNum == 12 ? jaarNum + 1 : jaarNum, monthNum == 12 ? 1 : monthNum + 1, 1, 23, 59, 59);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            return cal;
        }
    }

    private static class ReportJaar implements ReportEntry {

        private final int jaarNum;

        public ReportJaar(int jaarNum) {
            this.jaarNum = jaarNum;
        }

        @Override
        public String toString() {
            return "" + jaarNum;
        }

        @Override
        public final Calendar getStartDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(jaarNum, 0, 1, 0, 0, 0);
            return cal;
        }

        @Override
        public Calendar getEndDate() {
            Calendar cal = Calendar.getInstance();
            cal.set(jaarNum + 1, 0, 1, 23, 59, 59);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            return cal;
        }
    }
}
