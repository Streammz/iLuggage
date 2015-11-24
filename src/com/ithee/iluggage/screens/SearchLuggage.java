
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.*;
import com.ithee.iluggage.core.scene.PopupSceneController;
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
public class SearchLuggage extends PopupSceneController {
    private static final int MAX_DISPLAY_SIZE = 10;

    @FXML private ChoiceBox<LuggageKind> cbKind;
    @FXML private ChoiceBox<LuggageColor> cbColor;
    @FXML private TextField tfKeywords;
    @FXML private AutocompleteTextField tfBrand;
    
    @FXML private VBox results;
    
    @Override
    public void onCreate() {
        
        cbKind.getItems().add(null);
        app.dbKinds.getValues().forEach((o) -> {
            cbKind.getItems().add(o);
        });
        cbColor.getItems().add(null);
        app.dbColors.getValues().forEach((o) -> {
            cbColor.getItems().add(o);
        });
        app.dbBrands.getValues().forEach((o) -> {
            tfBrand.getEntries().add(o.name);
        });
        
        List<Luggage> list = app.db.executeAndReadList(Luggage.class, "SELECT * FROM `luggage`");
        System.out.println("list size = " + list.size());
        
        showResults(list);
    }
    
    @FXML public void onSearch() {
        String query = "SELECT * FROM `Luggage`";
        //if ()
    }
    
    public void showResults(List<Luggage> list) {
        this.results.getChildren().clear();
        int size = Math.min(list.size(), MAX_DISPLAY_SIZE);
        
        for (int i=0; i<list.size(); i++) {
            Luggage luggage = list.get(i);
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/ithee/iluggage/screens/CustomerListItem.fxml"));
                Parent parent = loader.load();
                CustomerListItem controller = loader.getController();
                controller.myLuggage = luggage;
                controller.root = parent;
                controller.app = this.app;
                controller.onCreate();
                
                this.results.getChildren().add(controller.root);
                if (i < list.size()-1) {
                    this.results.getChildren().add(new Separator(Orientation.HORIZONTAL));
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    
    
}
