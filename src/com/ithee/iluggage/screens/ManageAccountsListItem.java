package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.database.classes.Account;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class ManageAccountsListItem {
    private static final String DISABLE_ACCOUNT = "UPDATE `accounts` SET `Disabled` = 1 WHERE `Id` = ?";
    private static final String ENABLE_ACCOUNT = "UPDATE `accounts` SET `Disabled` = 0 WHERE `Id` = ?";
    

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    public ILuggageApplication app;
    public Account myAccount;
    public Parent root;
    public ManageAccounts parent;

    @FXML
    private VBox container;
    @FXML
    private Text txtName;
    @FXML
    private Text txtUsername;
    @FXML
    private Text txtRole;
    @FXML
    private Text txtLastLogin;

    public void onCreate() {
        txtName.setText(myAccount.name);
        txtUsername.setText(myAccount.username);
        txtRole.setText(app.getString("role_" + myAccount.permissionLevel));
        txtLastLogin.setText(DATE_FORMAT.format(myAccount.lastLogin));
        
        if (myAccount.disabled) {
            container.getStyleClass().add("disabled");
        } else {
            container.getStyleClass().add("enabled");
        }
    }

    public void onClick() {
        app.showPopupScene(AccountEdit.class).loadAccount(myAccount, () -> {
            parent.onSearch();
        });
    }

    public void onClickDelete() {
        boolean delete = app.showConfirmDialog("disable_account", myAccount.name);

        if (delete) {
            app.db.executeStatement(DISABLE_ACCOUNT, myAccount.id);
            // Verander de status
            app.changeStatus("account_disabled", myAccount.username);

            // Refresh the results
            parent.onSearch();
        }
    }
    
    public void onClickRestore() {
        boolean restore = app.showConfirmDialog("restore_account", myAccount.name);
        
        if (restore) {
            app.db.executeStatement(ENABLE_ACCOUNT, myAccount.id);
            // Verander de status
            app.changeStatus("account_restored", myAccount.username);
            // Refresh the results
            parent.onSearch();
        }
    }
}
