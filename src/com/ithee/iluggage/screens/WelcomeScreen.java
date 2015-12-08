package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SubSceneController;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * @author iThee
 */
public class WelcomeScreen extends SubSceneController {
    
    @FXML
    private Text tfNaam;

  
    @Override
    public void onCreate() {
        
                tfNaam.setText(app.getUser().name);

    }
    

}
