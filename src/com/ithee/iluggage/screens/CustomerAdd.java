package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class CustomerAdd extends SubSceneController {

    private static final String SQL_INSERT_CUSTOMER = "INSERT INTO `customers` VALUES ("
            + "NULL,  ?, ?, ?, ?, ?, ?, ?)";

    @FXML
    private TextField tfCustomername;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;
    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfPostalcode;
    @FXML
    private TextField tfHousenumber;
    @FXML
    private TextField tfAddition;

    public void onCancel(ActionEvent event) {
        app.switchSubScene(null);
    }

    public void onAdd(ActionEvent event) {

        Customer cus = new Customer();
        cus.name = tfCustomername.getText();
        cus.email = tfEmail.getText();
        cus.phone = tfPhone.getText();
        cus.housenumber = tfHousenumber.getText();
        cus.postalcode = tfPostalcode.getText();
        cus.address = tfAddress.getText();
        cus.addition = tfAddition.getText();

        app.db.executeStatement(SQL_INSERT_CUSTOMER,
                cus.name, cus.email, cus.phone, cus.address, cus.postalcode, cus.housenumber, cus.addition);

        app.switchSubScene(null);
    }

}
