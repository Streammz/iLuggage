package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.scene.SceneController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * @author iThee
 */
public class CustomerDetails extends SceneController {

    private static final String UPDATE_SQL
            = "UPDATE `customers` SET "
            + "`Name` = ?, `Email` = ?, `Phone` = ?, `Address` = ?, `Postalcode` = ?, "
            + "`Housenumber` = ?, `Addition` = ? "
            + "WHERE `Id` = ?";

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfPostalCode;
    @FXML
    private TextField tfHousenumber;
    @FXML
    private TextField tfAddition;

    private Customer myCustomer;
    private Runnable afterSave;

    public void loadCustomer(Customer customer, Runnable afterSave) {
        this.myCustomer = customer;
        this.afterSave = afterSave;

        tfName.setText(customer.name);
        tfEmail.setText(customer.email);
        tfPhone.setText(customer.phone);

        tfAddress.setText(customer.address);
        tfPostalCode.setText(customer.postalcode);
        tfHousenumber.setText(customer.housenumber);
        tfAddition.setText(customer.addition);
    }

    public void onSave() {
        myCustomer.name = tfName.getText();
        myCustomer.email = tfEmail.getText();
        myCustomer.phone = tfPhone.getText();
        myCustomer.address = tfAddress.getText();
        myCustomer.postalcode = tfPostalCode.getText();
        myCustomer.housenumber = tfHousenumber.getText();
        myCustomer.addition = tfAddition.getText();

        app.db.executeStatement(UPDATE_SQL, (statement) -> {
            statement.add(myCustomer.name);
            statement.add(myCustomer.email);
            statement.add(myCustomer.phone);
            statement.add(myCustomer.address);
            statement.add(myCustomer.postalcode);
            statement.add(myCustomer.housenumber);
            statement.add(myCustomer.addition);
            statement.add(myCustomer.id);
        });

        this.stage.close();
        // Verander de status
        app.changeStatus("customer_modified", myCustomer.name);
        if (afterSave != null) {
            afterSave.run();
        }
    }

}
