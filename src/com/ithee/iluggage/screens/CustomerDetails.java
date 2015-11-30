package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.scene.SceneController;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * @author iThee
 */
public class CustomerDetails extends SceneController {
    
    @FXML private Text txtName;
    @FXML private Text txtEmail;
    @FXML private Text txtPhone;
    
    @FXML private Text txtAddress;
    @FXML private Text txtPostalCode;
    @FXML private Text txtHousenumber;
    @FXML private Text txtAddition;
    
    public void loadCustomer(Customer customer) {
        txtName.setText(customer.name);
        txtEmail.setText(dashOrVal(customer.email));
        txtPhone.setText(dashOrVal(customer.phone));
        
        txtAddress.setText(dashOrVal(customer.address));
        txtPostalCode.setText(dashOrVal(customer.postalcode));
        txtHousenumber.setText(dashOrVal(customer.housenumber));
        txtAddition.setText(dashOrVal(customer.addition));
    }
    
    private String dashOrVal(String value) {
        return value == null ? "-" : value;
    }
    
}
