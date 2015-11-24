
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.*;
import com.ithee.iluggage.core.scene.PopupSceneController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * @author iThee
 */
public class SearchLuggage extends PopupSceneController {

    @FXML private ChoiceBox<LuggageKind> cbKind;
    @FXML private ChoiceBox<LuggageColor> cbColor;
    @FXML private TextField tfKeywords;
    @FXML private AutocompleteTextField tfBrand;
    
    @Override
    public void onCreate() {
        
        app.dbKinds.getValues().forEach((o) -> {
            cbKind.getItems().add(o);
        });
        app.dbColors.getValues().forEach((o) -> {
            cbColor.getItems().add(o);
        });
        app.dbBrands.getValues().forEach((o) -> {
            tfBrand.getEntries().add(o.name);
        });
        
        
    }

    
    
}
