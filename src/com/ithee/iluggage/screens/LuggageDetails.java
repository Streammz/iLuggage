package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.controls.SelectingTextField;
import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SceneController;
import java.text.SimpleDateFormat;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author iThee
 */
public class LuggageDetails extends SceneController {

    private static final String UPDATE_SQL = "UPDATE `luggage` SET "
            + "`Status` = ?, `CustomerId` = ?, `Kind` = ?, `Brand` = ?, `Stickers` = ?, "
            + "`Color` = ?, `Size` = ?, `Miscellaneous` = ? "
            + "WHERE `Id` = ?";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    @FXML
    private ChoiceBox<LuggageStatus> cbType;
    @FXML
    private TextField tfDate;
    @FXML
    private SelectingTextField<Customer> tfCustomer;

    @FXML
    private ChoiceBox<LuggageKind> cbKind;
    @FXML
    private AutocompleteTextField tfBrand;
    @FXML
    private CheckBox cbStickers;
    @FXML
    private AutocompleteTextField tfColor;
    @FXML
    private TextField tfSize1, tfSize2, tfSize3;

    @FXML
    private TextArea tfMisc;

    private Luggage myLuggage;
    private Runnable afterSave;
    private Customer selectedCustomer;

    @Override
    public void onCreate() {

    }

    public void loadLuggage(Luggage luggage, Runnable afterSave) {
        this.myLuggage = luggage;
        this.afterSave = afterSave;

        for (int i = 1; i <= 3; i++) {
            cbType.getItems().add(new LuggageStatus(i, app.getString("luggage_type_" + i)));
        }
        cbType.setValue(cbType.getItems().get(luggage.status - 1));

        tfDate.setText(dateFormat.format(luggage.date));
        tfDate.setDisable(true);

        List<Customer> customers = app.db.executeAndReadList(Customer.class, "SELECT * FROM `customers`");
        if (myLuggage.customerId != null) {
            selectedCustomer = customers.stream().filter((o) -> o.id == myLuggage.customerId).findFirst().get();
            tfCustomer.setText(selectedCustomer.name);
        }
        tfCustomer.getEntries().addAll(customers);
        tfCustomer.setOnSelect((customer) -> {
            this.selectedCustomer = customer;
            tfCustomer.setText(customer.name);
        });

        cbKind.getItems().addAll(app.dbKinds);
        cbKind.setValue(myLuggage.getKind(app));

        tfBrand.setText(myLuggage.getBrand(app).name);
        app.dbBrands.getValues().forEach((o) -> tfBrand.getEntries().add(o.toString()));

        cbStickers.setSelected(myLuggage.stickers);

        tfColor.setText(myLuggage.getColor(app).name);
        app.dbColors.getValues().forEach((o) -> tfColor.getEntries().add(o.toString()));

        double[] size = myLuggage.getSize();
        if (size != null) {
            tfSize1.setText(size[0] == (int) size[0] ? String.valueOf((int) size[0]) : String.valueOf(size[0]));
            tfSize2.setText(size[1] == (int) size[1] ? String.valueOf((int) size[1]) : String.valueOf(size[1]));
            tfSize3.setText(size[2] == (int) size[2] ? String.valueOf((int) size[2]) : String.valueOf(size[2]));
        }

        tfMisc.setText(myLuggage.miscellaneous);
    }

    public void onSave() {
        double[] sizes;
        try {
            sizes = getSizes();
        } catch (NumberFormatException ex) {
            return;
        }

        myLuggage.status = cbType.getValue().id;
        myLuggage.customerId = selectedCustomer == null ? null : selectedCustomer.id;
        myLuggage.setKind(cbKind.getValue());
        myLuggage.setBrand(app, tfBrand.getText());
        myLuggage.stickers = cbStickers.isSelected();
        myLuggage.setColor(app, tfColor.getText());
        myLuggage.setSize(sizes);
        myLuggage.miscellaneous = tfMisc.getText();

        app.db.executeStatement(UPDATE_SQL, (statement) -> {
            statement.add(myLuggage.status);
            statement.add(myLuggage.customerId);
            statement.add(myLuggage.kind);
            statement.add(myLuggage.brand);
            statement.add(myLuggage.stickers);
            statement.add(myLuggage.color);
            statement.add(myLuggage.size);
            statement.add(myLuggage.miscellaneous);
            statement.add(myLuggage.id);
        });

        this.stage.close();
        // Verander de status
        app.changeStatus("luggage_modified");
        if (afterSave != null) {
            afterSave.run();
        }
    }

    private double[] getSizes() throws NumberFormatException {
        if (tfSize1.getText().length() == 0 || tfSize2.getText().length() == 0 || tfSize3.getText().length() == 0) {
            return null;
        }
        return new double[]{
            Double.parseDouble(tfSize1.getText()),
            Double.parseDouble(tfSize2.getText()),
            Double.parseDouble(tfSize3.getText())
        };
    }

    public static class LuggageStatus {

        private int id;
        private String name;

        public LuggageStatus(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
