
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.PopupSceneController;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class FoundLuggage extends PopupSceneController {
    
    private static final String SQL_INSERT = "INSERT INTO `Luggage` VALUES ("
            + "NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
    
    @FXML private ChoiceBox<LuggageKind> chKind;
    @FXML private AutocompleteTextField tfBrand;
    @FXML private AutocompleteTextField tfColor;
    @FXML private TextField tfSize1;
    @FXML private TextField tfSize2;
    @FXML private TextField tfSize3;
    @FXML private CheckBox cbStickers;
    @FXML private TextField tfFlightnr;
    @FXML private TextArea tfMisc;

    @Override
    public void onCreate() {
        app.dbKinds.getValues().forEach((kind) -> {
            chKind.getItems().add(kind);
        });
        
        app.dbBrands.getValues().forEach((brand) -> {
            tfBrand.getEntries().add(brand.name);
        });
        
        app.dbColors.getValues().forEach((color) -> {
            tfColor.getEntries().add(color.name);
        });
    }
    
    public void onCancel(ActionEvent event) {
        this.stage.close();
    }

    public void onAdd(ActionEvent event) {
        
        double[] sizes;
        try {
            sizes = getSizes();
        } catch (NumberFormatException ex) {
            showSimpleMessage(Alert.AlertType.ERROR, "Grootte", 
                    "De ingevulde waarden voor \"Grootte\" zijn geen nummers");
            return;
        }
        
        
        Luggage lugg = new Luggage();
        lugg.flightCode = tfFlightnr.getText();
        lugg.setKind(chKind.getValue());
        lugg.setBrand(app, tfBrand.getText());
        lugg.setColor(app, tfColor.getText());
        lugg.setSize(sizes[0], sizes[1], sizes[2]);
        lugg.stickers = cbStickers.isSelected();
        lugg.miscellaneous = tfMisc.getText();
        lugg.date = new Date().toString();
        
        app.db.executeStatement(SQL_INSERT, 
                lugg.flightCode, lugg.kind, lugg.brand, lugg.color,
                lugg.size, lugg.stickers, lugg.miscellaneous, lugg.date);
        
        this.stage.close();
    }
    
    
    private double[] getSizes() throws NumberFormatException {
        return new double[] {
            Double.parseDouble(tfSize1.getText()),
            Double.parseDouble(tfSize2.getText()),
            Double.parseDouble(tfSize3.getText())
        };
    }
}
