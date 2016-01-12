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
            + "NULL, ?, ?, ?, ?, ?, ?, ?, 0)";

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

    private Runnable afterSave;

    @Override
    public void onCreate() {
        // Voeg de bestaande rollen toe aan het selectielijstje voor rollen.
        for (int i = 0; i < 3; i++) {
            cbRole.getItems().add(new Role(i, app.getString("role_" + i)));
        }
    }

    public void load(Runnable afterSave) {
        this.afterSave = afterSave;
    }

    /**
     * De onAction event die word aangeroepen als er op aanmaken word gedrukt.
     *
     * @param event De parameters van het event.
     */
    public void onAdd(ActionEvent event) {
        // Controleer of alle gegevens in de form correct zijn.
        if (!isFormValid()) {
            app.showErrorMessage("form_invalid");
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
        acc.id = app.db.executeStatement(SQL_INSERT_ACCOUNT, (statement) -> {
            statement.add(acc.username);
            statement.add(acc.password);
            statement.add(acc.salt);
            statement.add(acc.name);
            statement.add(acc.phone);
            statement.add(acc.permissionLevel);
            statement.add(acc.lastLogin);
        });

        if (acc.id > 0) {
            app.changeStatus("account_added", acc.username);
            // Sluit het scherm
            this.stage.close();
            if (afterSave != null) {
                afterSave.run();
            }
        } else if (app.db.lastError.matches("Duplicate entry .* for key 'Username'")) {
            // Controleert of de gebruikersnaam al in gebruik is
            app.showErrorMessage("account_already_exists", acc.username);
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
