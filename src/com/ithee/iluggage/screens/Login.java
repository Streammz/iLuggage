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
            // Controleert of het programma buiten het HvA word benaderd
            if (app.db.lastError.contains("Access denied for user")) {
                ILuggageApplication.showSimpleMessage(Alert.AlertType.ERROR,
                        "Geen verbinding met de database",
                        "Geen verbinding kunnen maken met de database van het HvA.\n"
                        + "Deze applicatie is op dit moment niet buiten het HvA te gebruiken.");
            } else {
                showSimpleMessage(AlertType.ERROR, "Foute inloggegevens", "Gebruikersnaam of wachtwoord komt niet overeen.");
            }
        }
    }

}
