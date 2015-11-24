
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.PopupSceneController;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class LostLuggage extends PopupSceneController {
    
    private static final String SQL_INSERT_CUSTOMER = "INSERT INTO `customers` VALUES ("
            + "NULL,  ?, ?, ?)";
    
    private static final String SQL_INSERT_LUGGAGE = "INSERT INTO `luggage` VALUES ("
            + "NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, 2)";
    
    @FXML private ChoiceBox<LuggageKind> chKind;
    @FXML private AutocompleteTextField tfBrand;
    @FXML private AutocompleteTextField tfColor;
    @FXML private TextField tfSize1;
    @FXML private TextField tfSize2;
    @FXML private TextField tfSize3;
    @FXML private CheckBox cbStickers;
    @FXML private TextField tfFlightnr;
    @FXML private TextArea tfMisc;
    @FXML private TextField tfCustomername;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;

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
        Customer cus = new Customer();
        cus.name = tfCustomername.getText();
        cus.email = tfEmail.getText();
        cus.phone = tfPhone.getText();
        
        app.db.executeStatement(SQL_INSERT_CUSTOMER, 
                cus.name, cus.email, cus.phone);
        cus.id = app.db.executeStatement(SQL_INSERT_CUSTOMER, (statement) -> {
            try {
                if (cus.name.length() == 0) statement.setNull(1, Types.VARCHAR);
                else statement.setString(1, cus.name);

                if (cus.email.length() == 0) statement.setNull(2, Types.VARCHAR);
                else statement.setString(2, cus.email);

                if (cus.phone.length() == 0) statement.setNull(3, Types.VARCHAR);
                else statement.setString(3, cus.phone);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        Luggage lugg = new Luggage();
        lugg.customerId = cus.id;
        lugg.flightCode = tfFlightnr.getText();
        lugg.setKind(chKind.getValue());
        lugg.setBrand(app, tfBrand.getText());
        lugg.setColor(app, tfColor.getText());
        lugg.setSize(sizes);
        lugg.stickers = cbStickers.isSelected();
        lugg.miscellaneous = tfMisc.getText();
        lugg.date = new Date();
        
        lugg.id = app.db.executeStatement(SQL_INSERT_LUGGAGE, (statement) -> {
            try {
                statement.setInt(1, lugg.customerId);
                
                if (lugg.flightCode.length() == 0) statement.setNull(2, Types.VARCHAR);
                else statement.setString(2, lugg.flightCode);
                
                if (lugg.kind == null) statement.setNull(3, Types.INTEGER);
                else statement.setInt(3, lugg.kind);
                
                if (lugg.brand == null) statement.setNull(4, Types.INTEGER);
                else statement.setInt(4, lugg.brand);
                
                if (lugg.color == null) statement.setNull(5, Types.INTEGER);
                else statement.setInt(5, lugg.color);
                
                if (lugg.size == null) statement.setNull(6, Types.VARCHAR);
                else statement.setString(6, lugg.size);
                
                statement.setBoolean(7, lugg.stickers);
                
                if (lugg.miscellaneous.length() == 0) statement.setNull(8, Types.VARCHAR);
                else statement.setString(8, lugg.miscellaneous);
                
                statement.setDate(9, new java.sql.Date(lugg.date.getTime()));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        this.stage.close();
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
