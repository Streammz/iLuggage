
package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.database.classes.Customer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

/**
 *
 * @author robby
 */
public class SearchCustomerListItem {
        
    public ILuggageApplication app;
    public Customer myCustomer;
    public Parent root;
    
    @FXML private Text txtName;
    @FXML private Text txtEmail;
    @FXML private Text txtPhone;
    @FXML private Text txtAddress;
    @FXML private Text txtPostalCode;
    @FXML private Text txtHouseNumber;
    @FXML private Text txtAddition;

    public void onCreate() {
        txtName.setText(myCustomer.name);
        txtEmail.setText(myCustomer.email == null ? "-" : myCustomer.email);
        txtPhone.setText(myCustomer.phone == null ? "-" : myCustomer.phone);
        txtAddress.setText(myCustomer.address == null ? "-" : myCustomer.address);
        txtPostalCode.setText(myCustomer.postalcode == null ? "-" : myCustomer.postalcode);
        txtHouseNumber.setText(myCustomer.housenumber == null ? "-" : myCustomer.housenumber);
        txtAddition.setText(myCustomer.addition == null ? "-" : myCustomer.addition);
    }
    
    public void onClick() {
        ILuggageApplication.showSimpleMessage(AlertType.INFORMATION, 
                "Missende functionaliteit", "Deze functionaliteit ontbreekt nog.");
    }
}
