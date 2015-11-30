
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
public class AccountEdit extends SceneController {
    private static final String DO_NOT_UPDATE = "DONOTUPDATE";
    private static final String SQL_UPDATE_ACCOUNT = "UPDATE `accounts` SET "
            + "`Password` = ?, `Salt` = ?, `Name` = ?, `Phone` = ?, `PermissionLevel` = ? "
            + "WHERE `Id` = ?";
    
    Account myAcc;

    @FXML private TextField tfName;
    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private TextField tfPhone;
    @FXML private ChoiceBox<Role> cbRole;

    @Override
    public void onCreate() {
        cbRole.getItems().add(new Role(0, "Medewerker"));
        cbRole.getItems().add(new Role(1, "Manager"));
        cbRole.getItems().add(new Role(2, "Administrator"));
        tfUsername.setDisable(true);
    }
    
    public void loadAccount(Account account) {
        this.myAcc = account;
        tfName.setText(account.name);
        tfUsername.setText(account.username);
        tfPassword.setText(DO_NOT_UPDATE);
        tfPhone.setText(account.phone);
        cbRole.setValue(cbRole.getItems().get(account.permissionLevel));
    }
    
    

    public void onSave(ActionEvent event) {
        if (!isFormValid()) {
            showSimpleMessage(Alert.AlertType.ERROR, "Ontbrekende gegevens", "Niet alle velden zijn (correct) ingevuld.");
        }
        
        myAcc.name = tfName.getText();
        if (!tfPassword.getText().equals(DO_NOT_UPDATE)) {
            myAcc.salt = PasswordHasher.generateSalt();
            myAcc.password = PasswordHasher.generateHash(myAcc.salt + tfPassword.getText());
        }
        myAcc.phone = tfPhone.getLength() == 0 ? null : tfPhone.getText();
        myAcc.permissionLevel = cbRole.getValue().roleId;
        
        app.db.executeStatement(SQL_UPDATE_ACCOUNT, 
                myAcc.password, myAcc.salt, myAcc.name, myAcc.phone, myAcc.permissionLevel, myAcc.id);
        
        this.stage.close();
    }
    
    public boolean isFormValid() {
        if (tfName.getLength() == 0) return false;
        if (tfUsername.getLength() == 0) return false;
        if (tfPassword.getLength() == 0) return false;
        if (cbRole.getValue() == null) return false;
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
