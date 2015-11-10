package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 *
 * @author Robby
 */
public class MainMenu extends SceneController {

    @FXML Text loggedinUser;
    
    @Override
    public void onCreate() {
        loggedinUser.setText("Test Admin");
    }
    
    
    
    public void onLogoutPress() {
        System.out.println("Log out here");
    }
    
}
