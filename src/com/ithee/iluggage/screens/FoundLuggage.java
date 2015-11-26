
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class FoundLuggage extends SubSceneController {
    
    private static final String SQL_INSERT = "INSERT INTO `luggage` VALUES ("
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
        chKind.getItems().add(null);
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
        app.switchSubScene(null);
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
        lugg.setSize(sizes);
        lugg.stickers = cbStickers.isSelected();
        lugg.miscellaneous = tfMisc.getText();
        lugg.date = new Date();
        
        app.db.executeStatement(SQL_INSERT, (statement) -> {
            try {
                if (lugg.flightCode.length() == 0) statement.setNull(1, Types.VARCHAR);
                else statement.setString(1, lugg.flightCode);
                
                if (lugg.kind == null) statement.setNull(2, Types.INTEGER);
                else statement.setInt(2, lugg.kind);
                
                if (lugg.brand == null) statement.setNull(3, Types.INTEGER);
                else statement.setInt(3, lugg.brand);
                
                if (lugg.color == null) statement.setNull(4, Types.INTEGER);
                else statement.setInt(4, lugg.color);
                
                if (lugg.size == null) statement.setNull(5, Types.VARCHAR);
                else statement.setString(5, lugg.size);
                
                statement.setBoolean(6, lugg.stickers);
                if (lugg.miscellaneous.length() == 0) statement.setNull(7, Types.VARCHAR);
                else statement.setString(7, lugg.miscellaneous);
                
                statement.setDate(8, new java.sql.Date(lugg.date.getTime()));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        app.switchSubScene(null);
    }
    
    
    private double[] getSizes() throws NumberFormatException {
        if (tfSize1.getText().length() == 0 || tfSize2.getText().length() == 0 || tfSize3.getText().length() == 0) return null;
        return new double[] {
            Double.parseDouble(tfSize1.getText()),
            Double.parseDouble(tfSize2.getText()),
            Double.parseDouble(tfSize3.getText())
        };
    }
}
