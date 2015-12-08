package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.database.classes.Customer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class SearchCustomerListItem {

    public ILuggageApplication app;
    public Customer myCustomer;
    public Parent root;
    public SearchCustomer parent;

    @FXML
    private Text txtName;
    @FXML
    private Text txtEmail;
    @FXML
    private Text txtPhone;
    @FXML
    private Text txtAddress;
    @FXML
    private Text txtPostalCode;
    @FXML
    private Text txtHouseNumber;
    @FXML
    private Text txtAddition;

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
        app.showPopupScene(CustomerDetails.class).loadCustomer(myCustomer, () -> {
            parent.onSearch();
        });
    }

    public void onClickDelete() {
        boolean delete = app.showConfirmDialog("delete_customer", myCustomer.name);

        if (delete) {
            app.db.executeStatement("DELETE FROM `customers` WHERE `Id` = ?", myCustomer.id);
            // Refresh the results
            parent.onSearch();
        }
    }
}
