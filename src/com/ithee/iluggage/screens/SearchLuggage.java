package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.*;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author iThee
 */
public class SearchLuggage extends SubSceneController {

    private static final int MAX_DISPLAY_SIZE = 25;

    @FXML
    private ChoiceBox<LuggageKind> cbKind;
    @FXML
    private ChoiceBox<LuggageColor> cbColor;
    @FXML
    private ChoiceBox<LuggageBrand> cbBrand;
    @FXML
    private TextField tfKeywords;

    @FXML
    private VBox results;

    @Override
    public void onCreate() {

        cbKind.getItems().add(null);
        app.dbKinds.forEach((o) -> {
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

        List<Luggage> list = app.db.executeAndReadList(Luggage.class, "SELECT * FROM `luggage`");
        showResults(list);
    }

    @FXML
    public void onSearch() {
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

        List<Luggage> list = app.db.executeAndReadList(Luggage.class, query, params.toArray());
        showResults(list);
    }
          public static void searchByCustomer(int customerID){
            
        }

    public void showResults(List<Luggage> list) {
        // Clear old list (if there is anything in it)
        this.results.getChildren().clear();

        int size = Math.min(list.size(), MAX_DISPLAY_SIZE);
        for (int i = 0; i < size; i++) {
            Luggage luggage = list.get(i);
            try {
                //Load a new item (SearchLuggageListItem.java) so that it can be displayed in the list
                FXMLLoader loader = new FXMLLoader();
                loader.setResources(app.getLanguage());
                loader.setLocation(getClass().getResource("/com/ithee/iluggage/screens/SearchLuggageListItem.fxml"));
                Parent parent = loader.load();
                SearchLuggageListItem controller = loader.getController();
                controller.myLuggage = luggage;
                controller.root = parent;
                controller.app = this.app;
                controller.parent = this;
                controller.onCreate();

                // Add to the list and style
                this.results.getChildren().add(controller.root);

                // Add a seperator between items
                //if (i < list.size()-1) {
                this.results.getChildren().add(new Separator(Orientation.HORIZONTAL));
                //}
            } catch (Exception ex) {
                // This should not happen in regular usage.
                throw new RuntimeException(ex);
            }
        }
    }

}
