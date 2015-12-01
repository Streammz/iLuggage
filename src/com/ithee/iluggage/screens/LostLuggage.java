
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.controls.SelectingTextField;
import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class LostLuggage extends SubSceneController {
    
    private static final String SQL_INSERT_CUSTOMER = "INSERT INTO `customers` VALUES ("
            + "NULL,  ?, ?, ?, ?, ?, ?, ?)";
    
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
    @FXML private SelectingTextField<Customer> tfCustomername;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;
    @FXML private TextField tfAddress;
    @FXML private TextField tfPostalCode;
    @FXML private TextField tfHousenumber;
    @FXML private TextField tfAddition;

    private List<Customer> customers;
    private Customer selectedCustomer;
    
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
        
        customers = app.db.executeAndReadList(Customer.class, "SELECT * FROM `customers`");
        tfCustomername.getEntries().addAll(customers);
        tfCustomername.setOnSelect((customer) -> {
            tfEmail.setDisable(true);
            tfPhone.setDisable(true);
            tfAddress.setDisable(true);
            tfPostalCode.setDisable(true);
            tfHousenumber.setDisable(true);
            tfAddition.setDisable(true);
            tfCustomername.setText(customer.name);
            tfEmail.setText(customer.email);
            tfPhone.setText(customer.phone);
            tfAddress.setText(customer.address);
            tfPostalCode.setText(customer.postalcode);
            tfHousenumber.setText(customer.housenumber);
            tfAddition.setText(customer.addition);
            
            selectedCustomer = customer;
            
            tfCustomername.textProperty().addListener(new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    tfEmail.setText(null);
                    tfPhone.setText(null);
                    tfAddress.setText(null);
                    tfPostalCode.setText(null);
                    tfHousenumber.setText(null);
                    tfAddition.setText(null);
                    tfEmail.setDisable(false);
                    tfPhone.setDisable(false);
                    tfAddress.setDisable(false);
                    tfPostalCode.setDisable(false);
                    tfHousenumber.setDisable(false);
                    tfAddition.setDisable(false);
                    tfCustomername.textProperty().removeListener(this);
                    
                    selectedCustomer = null;
                }
            });
        });
    }
    
    public void onCancel(ActionEvent event) {
        app.switchSubScene(null);
    }

    public void onAdd(ActionEvent event) {
        
        if (!isFormValid()) {
            showSimpleMessage(Alert.AlertType.ERROR, "Foutieve gegevens.", 
                    "Niet alle gegevens zijn (correct) ingevoerd.");
        }
        
        double[] sizes;
        try {
            sizes = getSizes();
        } catch (NumberFormatException ex) {
            return;
        }
        
        Customer cus;
        if (this.selectedCustomer != null) {
            cus = this.selectedCustomer;
        } else {
            cus = new Customer();
            cus.name = tfCustomername.getText();
            cus.email = tfEmail.getText();
            cus.phone = tfPhone.getText();
            cus.address = tfAddress.getText();
            cus.postalcode = tfPostalCode.getText();
            cus.housenumber = tfHousenumber.getText();
            cus.addition = tfAddition.getText();
            
            cus.id = app.db.executeStatement(SQL_INSERT_CUSTOMER, (statement) -> {
                statement.add(cus.name);
                statement.add(cus.email);
                statement.add(cus.phone);
                statement.add(cus.address);
                statement.add(cus.postalcode);
                statement.add(cus.housenumber);
                statement.add(cus.addition);
            });
        }
        if (cus.id == -1) return;
        
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
            statement.add(lugg.customerId);
            statement.add(lugg.flightCode);
            statement.add(lugg.kind);
            statement.add(lugg.brand);
            statement.add(lugg.color);
            statement.add(lugg.size);
            statement.add(lugg.stickers);
            statement.add(lugg.miscellaneous);
            statement.add(lugg.date);
        });
        
        app.switchSubScene(null);
    }
    
    public boolean isFormValid() {
        if (this.selectedCustomer != null) {
            if (this.tfCustomername.getLength() == 0) return false;
            if (this.tfEmail.getLength() == 0) return false;
            if (this.tfAddress.getLength() == 0) return false;
            if (this.tfPostalCode.getLength() == 0) return false;
            if (this.tfHousenumber.getLength() == 0) return false;
        }
        if (this.chKind.getValue() == null) return false;
        
        try {
            getSizes();
        } catch (NumberFormatException ex) {
            return false;
        }
        
        return true;
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
