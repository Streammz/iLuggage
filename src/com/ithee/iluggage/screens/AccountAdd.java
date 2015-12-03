package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Account;
import com.ithee.iluggage.core.scene.SceneController;
import com.ithee.iluggage.core.security.PasswordHasher;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller voor het scherm om een account toe te voegen. Dit is een pop-up
 * scherm.
 *
 * @author iThee
 */
public class AccountAdd extends SceneController {

    /**
     * De query die gebruikt word om een account in te voeren in de database.
     */
    private static final String SQL_INSERT_ACCOUNT = "INSERT INTO `accounts` VALUES ("
            + "NULL, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Het veld voor de naam van het aan te maken account.
     */
    @FXML
    private TextField tfName;

    /**
     * Het veld voor de gebruikersnaam voor het aan te maken account.
     */
    @FXML
    private TextField tfUsername;

    /**
     * Het veld voor het wachtwoord voor het aan te maken account.
     */
    @FXML
    private PasswordField tfPassword;

    /**
     * Het veld voor het telefoonnummer voor het aan te maken account.
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
    }

    /**
     * De onAction event die word aangeroepen als er op aanmaken word gedrukt.
     *
     * @param event De parameters van het event.
     */
    public void onAdd(ActionEvent event) {
        // Controleer of alle gegevens in de form correct zijn.
        if (!isFormValid()) {
            showSimpleMessage(Alert.AlertType.ERROR, "Ontbrekende gegevens", "Niet alle velden zijn (correct) ingevuld.");
            return;
        }

        // Maak een nieuw account object aan, aan de hand van de ingevulde gegevens.
        Account acc = new Account();
        acc.name = tfName.getText();
        acc.username = tfUsername.getText();
        acc.salt = PasswordHasher.generateSalt();
        acc.password = PasswordHasher.generateHash(acc.salt + tfPassword.getText());
        acc.phone = tfPhone.getLength() == 0 ? null : tfPhone.getText();
        acc.permissionLevel = cbRole.getValue().roleId;
        acc.lastLogin = new Date(0L);

        // Voer de query uit en sla het gegenereerde ID op.
        acc.id = app.db.executeStatement(SQL_INSERT_ACCOUNT,
                acc.username, acc.password, acc.salt, acc.name, acc.phone, acc.permissionLevel, acc.lastLogin);

        if (acc.id > 0) {
            // Sluit het scherm
            this.stage.close();
        } else if (app.db.lastError.matches("Duplicate entry .* for key 'Username'")) {
            // Controleert of het gebruikersnaam al in gebruik is
            showSimpleMessage(Alert.AlertType.ERROR, "Gebruikersnaam bestaat al",
                    "De gebruikersnaam \"" + acc.username + "\" is al gebruikt.");
        }
    }

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
