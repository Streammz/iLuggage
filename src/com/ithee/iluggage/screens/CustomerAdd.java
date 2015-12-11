package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.scene.SubSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller voor het scherm om een klant toe te voegen.
 *
 * @author iThee
 */
public class CustomerAdd extends SubSceneController {

    /**
     * De query die gebruikt word om een klant toe te voegen aan de database.
     */
    private static final String SQL_INSERT_CUSTOMER = "INSERT INTO `customers` VALUES ("
            + "NULL,  ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Het veld voor de naam van de in te voeren klant.
     */
    @FXML
    private TextField tfCustomername;

    /**
     * Het veld voor het e-mailadres van de in te voeren klant.
     */
    @FXML
    private TextField tfEmail;

    /**
     * Het veld voor het telefoonnummer van de in te voeren klant.
     */
    @FXML
    private TextField tfPhone;

    /**
     * Het veld voor de woonplaats van de in te voeren klant.
     */
    @FXML
    private TextField tfAddress;

    /**
     * Het veld voor de postcode van de in te voeren klant.
     */
    @FXML
    private TextField tfPostalcode;

    /**
     * Het veld voor het huisnummer van de in te voeren klant.
     */
    @FXML
    private TextField tfHousenumber;

    /**
     * Het veld voor de toevoeging van het huisnummer van de in te voeren klant.
     */
    @FXML
    private TextField tfAddition;

    /**
     * Het onAction event dat word aangeroepen als er op annuleren word gedrukt.
     *
     * @param event De parameters van het event.
     */
    public void onCancel(ActionEvent event) {
        app.switchSubScene(null);
    }

    /**
     * Het onAction event dat word aangeroepen als er op toevoegen word gedrukt.
     *
     * @param event De parameters van het event.
     */
    public void onAdd(ActionEvent event) {
        if (!isFormValid()) {
            app.showErrorMessage("invalid_form");
            return;
        }
        // Maakt een nieuwe customer aan, aan de hand van de ingevulde velden.
        Customer cus = new Customer();
        cus.name = tfCustomername.getText();
        cus.email = tfEmail.getText();
        cus.phone = tfPhone.getLength() == 0 ? null : tfPhone.getText();
        cus.housenumber = tfHousenumber.getText();
        cus.postalcode = tfPostalcode.getText();
        cus.address = tfAddress.getText();
        cus.addition = tfAddition.getLength() == 0 ? null : tfAddition.getText();

        app.db.executeStatement(SQL_INSERT_CUSTOMER,
                cus.name, cus.email, cus.phone, cus.address, cus.postalcode, cus.housenumber, cus.addition);

        app.switchSubScene(null);
    }

    private boolean isFormValid() {
        // Controleert of de naam niet leeg is
        if (tfCustomername.getLength() == 0) {
            return false;
        }
        // Controleert of het e-mailadres niet leeg is
        if (tfEmail.getLength() == 0) {
            return false;
        }
        // Controleert of het huisnummer niet leeg is
        if (tfHousenumber.getLength() == 0) {
            return false;
        }
        // Controleert of de postcode niet leeg is
        if (tfPostalcode.getLength() == 0) {
            return false;
        }
        // Controleert of het woonplaats niet leeg is
        if (tfAddress.getLength() == 0) {
            return false;
        }
        return true;
    }

}
