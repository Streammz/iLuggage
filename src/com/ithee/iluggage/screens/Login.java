package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author iThee
 */
public class Login extends SceneController {

    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField tfPassword;

    @FXML
    public void onLoginPressed(ActionEvent event) {
        if (app.tryLogin(tfUsername.getText(), tfPassword.getText())) {
            app.switchMainScene(MainMenu.class);
        } else // Controleert of het programma buiten het HvA wordt benaderd
        {
            if (app.db.lastError.contains("Access denied for user")) {
                app.showErrorMessage("no_hva_connection");
            } else {
                app.showErrorMessage("invalid_login");
            }
        }
    }

}
