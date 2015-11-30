package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class MainMenu extends SceneController {
    
    @FXML private Text adminText, mngrText;
    @FXML private Button adminButton, mngrButton;
    @FXML private Text loggedinUser;
    
    @Override
    public void onCreate() {
        loggedinUser.setText(app.getUser().name);
        
        if (!app.isUserManager()) {
            removeNode(mngrButton, mngrText, adminButton, adminText);
        } else if (!app.isUserAdmin()) {
            removeNode(adminButton, adminText);
        }
    }
    

    public void onPressFoundLuggage(ActionEvent event) {
        app.switchSubScene(FoundLuggage.class);
    }

    public void onPressLostLuggage(ActionEvent event) {
        app.switchSubScene(LostLuggage.class);
    }

    public void onPressSearchLuggage(ActionEvent event) {
        app.switchSubScene(SearchLuggage.class);
    }

    public void onPressAddCustomer(ActionEvent event) {
        app.switchSubScene(CustomerAdd.class);
    }

    public void onPressSearchCustomer(ActionEvent event) {
        app.switchSubScene(SearchCustomer.class);
    }

    public void onPressReport(ActionEvent event) {
        showSimpleMessage(Alert.AlertType.INFORMATION, "Ontbrekende functionaliteit", "Deze functionaliteit ontbreekt nog.");
    }

    public void onPressManageUsers(ActionEvent event) {
        showSimpleMessage(Alert.AlertType.INFORMATION, "Ontbrekende functionaliteit", "Deze functionaliteit ontbreekt nog.");
    }

    public void onPressLogout(ActionEvent event) {
        app.logOut();
        app.switchMainScene(Login.class);
    }
    
}
