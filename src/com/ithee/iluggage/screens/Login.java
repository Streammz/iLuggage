
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author robby
 */
public class Login extends SceneController {
    @FXML private TextField tfUsername;
    @FXML private PasswordField tfPassword;

    @FXML
    public void onLoginPressed(ActionEvent event) {
        System.out.println("Log in with username " + tfUsername.getText() + " & pass " + tfPassword.getText().hashCode());
        app.tryLogin(tfUsername.getText(), tfPassword.getText());
    }
    
}
