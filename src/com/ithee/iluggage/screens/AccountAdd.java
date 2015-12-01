package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Account;
import com.ithee.iluggage.core.scene.SceneController;
import com.ithee.iluggage.core.security.PasswordHasher;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class AccountAdd extends SceneController {

    private static final String SQL_INSERT_ACCOUNT = "INSERT INTO `accounts` VALUES ("
            + "NULL, ?, ?, ?, ?, ?, ?, ?)";

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private TextField tfPhone;
    @FXML
    private ChoiceBox<Role> cbRole;

    @Override
    public void onCreate() {
        cbRole.getItems().add(new Role(0, "Medewerker"));
        cbRole.getItems().add(new Role(1, "Manager"));
        cbRole.getItems().add(new Role(2, "Administrator"));
    }

    public void onAdd(ActionEvent event) {
        if (!isFormValid()) {
            showSimpleMessage(Alert.AlertType.ERROR, "Ontbrekende gegevens", "Niet alle velden zijn (correct) ingevuld.");
        }

        Account acc = new Account();
        acc.name = tfName.getText();
        acc.username = tfUsername.getText();
        acc.salt = PasswordHasher.generateSalt();
        acc.password = PasswordHasher.generateHash(acc.salt + tfPassword.getText());
        acc.phone = tfPhone.getLength() == 0 ? null : tfPhone.getText();
        acc.permissionLevel = cbRole.getValue().roleId;
        acc.lastLogin = new Date(0L);

        acc.id = app.db.executeStatement(SQL_INSERT_ACCOUNT,
                acc.username, acc.password, acc.salt, acc.name, acc.phone, acc.permissionLevel, acc.lastLogin);

        if (app.db.lastError.matches("Duplicate entry .* for key 'Username'")) {
            showSimpleMessage(Alert.AlertType.ERROR, "Gebruikersnaam bestaat al",
                    "De gebruikersnaam \"" + acc.username + "\" is al gebruikt.");
            return;
        }

        if (acc.id > 0) {
            this.stage.close();
        }
    }

    public boolean isFormValid() {
        if (tfName.getLength() == 0) {
            return false;
        }
        if (tfUsername.getLength() == 0) {
            return false;
        }
        if (tfPassword.getLength() == 0) {
            return false;
        }
        if (cbRole.getValue() == null) {
            return false;
        }
        return true;
    }

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
