package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class MainMenu extends SceneController {
    
    @FXML private Text adminText;
    @FXML private VBox rightSideBox;
    @FXML private Button adminButton;
    @FXML private Text loggedinUser;
    
    @Override
    public void onCreate() {
        loggedinUser.setText(app.getUser().name);
        
        if (!app.isUserManager()) {
            removeNode(rightSideBox);
        } else if (!app.isUserAdmin()) {
            removeNode(adminButton, adminText);
        }
    }
    

    public void onPressFoundLuggage(ActionEvent event) {
        app.openScene(FoundLuggage.class);
    }

    public void onPressLostLuggage(ActionEvent event) {
        app.openScene(LostLuggage.class);
    }

    public void onPressSearchLuggage(ActionEvent event) {
    }

    public void onPressAddCustomer(ActionEvent event) {
    }

    public void onPressSearchCustomer(ActionEvent event) {
    }

    public void onPressReport(ActionEvent event) {
    }

    public void onPressManageUsers(ActionEvent event) {
    }

    public void onPressLogout(ActionEvent event) {
        app.logOut();
        app.switchMainScene(Login.class);
    }
    
}
