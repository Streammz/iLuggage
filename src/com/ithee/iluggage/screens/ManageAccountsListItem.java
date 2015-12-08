package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.database.classes.Account;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class ManageAccountsListItem {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    public ILuggageApplication app;
    public Account myAccount;
    public Parent root;
    public ManageAccounts parent;

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
    }

    public void onClick() {
        app.showPopupScene(AccountEdit.class).loadAccount(myAccount);
    }

    public void onClickDelete() {
        boolean delete = app.showConfirmDialog("delete_account", myAccount.name);

        if (delete) {
            app.db.executeStatement("DELETE FROM `accounts` WHERE `Id` = ?", myAccount.id);
            // Refresh the results
            parent.onSearch();
        }
    }
}
