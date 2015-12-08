package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Account;
import com.ithee.iluggage.core.scene.SceneController;
import com.ithee.iluggage.core.security.PasswordHasher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller voor het scherm om een account te bewerken. Dit is een pop-up
 * scherm.
 *
 * @author iThee
 */
public class AccountEdit extends SceneController {

    /**
     * De string die gebruikt wordt in het wachtwoordveld om het wachtwoord niet
     * te veranderen.
     */
    private static final String DO_NOT_UPDATE = "DONOTUPDATE";

    /**
     * De query die gebruikt wordt om een account bij te werken in de database.
     */
    private static final String SQL_UPDATE_ACCOUNT = "UPDATE `accounts` SET "
            + "`Password` = ?, `Salt` = ?, `Name` = ?, `Phone` = ?, `PermissionLevel` = ? "
            + "WHERE `Id` = ?";

    /**
     * Het account dat bewerkt wordt.
     */
    private Account myAcc;

    /**
     * Het veld voor de naam van het te bewerken account.
     */
    @FXML
    private TextField tfName;

    /**
     * Het veld voor de gebruikersnaam van het te bewerken account.
     */
    @FXML
    private TextField tfUsername;

    /**
     * Het veld voor het wachtwoord van het te bewerken account.
     */
    @FXML
    private PasswordField tfPassword;

    /**
     * Het veld voor het telefoonnummer van het te bewerken account.
     */
    @FXML
    private TextField tfPhone;

    /**
     * Het selectielijstje voor de rol van het account.
     */
    @FXML
    private ChoiceBox<Role> cbRole;

    @Override
    public void onCreate() {
        // Voeg de bestaande rollen toe aan het selectielijstje voor rollen.
        cbRole.getItems().add(new Role(0, "Medewerker"));
        cbRole.getItems().add(new Role(1, "Manager"));
        cbRole.getItems().add(new Role(2, "Administrator"));

        // Zet het gebruikersnaamveld uit, deze is niet bewerkbaar.
        tfUsername.setDisable(true);
    }

    /**
     * Laadt een bestaand account in de velden en zorgt dat het meegegeven
     * account bewerkbaar is.
     *
     * @param account Het account dat bewerkt moet worden.
     */
    public void loadAccount(Account account) {
        this.myAcc = account;
        tfName.setText(account.name);
        tfUsername.setText(account.username);
        tfPassword.setText(DO_NOT_UPDATE);
        tfPhone.setText(account.phone);
        cbRole.setValue(cbRole.getItems().get(account.permissionLevel));
    }

    /**
     * De onAction event die word aangeroepen als er op opslaan word gedrukt.
     * 
     * @param event De parameters van het event.
     */
    public void onSave(ActionEvent event) {
        // Controleer of alle gegevens in de form correct zijn.
        if (!isFormValid()) {
            showSimpleMessage(Alert.AlertType.ERROR, "Ontbrekende gegevens", "Niet alle velden zijn (correct) ingevuld.");
        }

        // Veranderd de gegevens van het account aan de hand van de ingevulde gegevens.
        myAcc.name = tfName.getText();
        if (!tfPassword.getText().equals(DO_NOT_UPDATE)) {
            myAcc.salt = PasswordHasher.generateSalt();
            myAcc.password = PasswordHasher.generateHash(myAcc.salt + tfPassword.getText());
        }
        myAcc.phone = tfPhone.getLength() == 0 ? null : tfPhone.getText();
        myAcc.permissionLevel = cbRole.getValue().roleId;

        // Voer de query uit.
        app.db.executeStatement(SQL_UPDATE_ACCOUNT,
                myAcc.password, myAcc.salt, myAcc.name, myAcc.phone, myAcc.permissionLevel, myAcc.id);

        // Sluit het scherm
        this.stage.close();
    }

    /**
     * Controleert of alle velden binnen het formulier correct zijn ingevuld.
     * @return True als de velden correct zijn ingevuld, anders false.
     */
    public boolean isFormValid() {
        // Controleert of de naam niet leeg is
        if (tfName.getLength() == 0) {
            return false;
        }
        // Controleert of de gebruikersnaam niet leeg is 
        if (tfUsername.getLength() == 0) {
            return false;
        }
        // Controleert of het wachtwoord niet leeg is
        if (tfPassword.getLength() == 0) {
            return false;
        }
        // Controleert of het geselecteerde rol niet leeg is
        if (cbRole.getValue() == null) {
            return false;
        }
        return true;
    }

    /**
     * Een rol voor in de selectielijst, met id en naam. Heeft een toString
     * functie die gebruikt wordt als weergave.
     */
    private static final class Role {

        private final int roleId;
        private final String roleName;

        public Role(int roleId, String roleName) {
            this.roleId = roleId;
            this.roleName = roleName;
        }

        @Override
        public String toString() {
            return roleName;
        }
    }
}
