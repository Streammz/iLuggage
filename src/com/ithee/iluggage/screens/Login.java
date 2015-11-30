
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author iThee
 */
public class Login extends SceneController {
    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;

    @FXML
    public void onLoginPressed(ActionEvent event) {
        if (app.tryLogin(tfUsername.getText(), tfPassword.getText())) {
            app.switchMainScene(MainMenu.class);
        } else {
            showSimpleMessage(AlertType.ERROR, "Foute inloggegevens", "Gebruikersnaam of wachtwoord komt niet overeen.");
        }
    }
    
}
