
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.PopupSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author robby
 */
public class FoundLuggage extends PopupSceneController {
    
    @FXML private ChoiceBox<?> chKind;
    @FXML private TextField tfColor;
    @FXML private TextField tfSize3;
    @FXML private TextField tfSize1;
    @FXML private CheckBox cbStickers;
    @FXML private TextArea tfMisc;
    @FXML private TextField tfSize2;
    @FXML private TextField tfBrand;

    public void initialize() {
        // Fill chKind
    }
    
    public void onCancel(ActionEvent event) {
        this.stage.close();
    }

    public void onAdd(ActionEvent event) {
        this.stage.close();
    }
}
