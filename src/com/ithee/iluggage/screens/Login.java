package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        } else {
            // Controleert of het programma buiten het HvA wordt benaderd
            if (app.db.lastError.contains("Access denied for user")) {
                ILuggageApplication.showSimpleMessage(Alert.AlertType.ERROR,
                        "No connection with the database",
                        "Couldn't make a connection with HvA database.\n"
                        + "At the moment it's not possible to use this application outside HvA.");
            } else {
                showSimpleMessage(AlertType.ERROR, "Incorrect username and/or password", "Please fill in the correct credentials");
            }
        }
    }

}
