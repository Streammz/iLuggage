
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.PopupSceneController;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author robby
 */
public class FoundLuggage extends PopupSceneController {
    
    @FXML private ChoiceBox<LuggageKind> chKind;
    @FXML private AutocompleteTextField tfBrand;
    @FXML private TextField tfColor;
    @FXML private TextField tfSize1;
    @FXML private TextField tfSize2;
    @FXML private TextField tfSize3;
    @FXML private CheckBox cbStickers;
    @FXML private TextArea tfMisc;

    @Override
    public void onCreate() {
        app.dbKinds.getValues().forEach((kind) -> {
            chKind.getItems().add(kind);
        });
        
        app.dbBrands.getValues().forEach((brand) -> {
            tfBrand.getEntries().add(brand.name);
        });
    }
    
    public void onCancel(ActionEvent event) {
        this.stage.close();
    }

    public void onAdd(ActionEvent event) {
        this.stage.close();
    }
}
