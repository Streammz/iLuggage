
package com.ithee.iluggage.core.scene;

import com.ithee.iluggage.ILuggageApplication;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author robby
 */
public abstract class SceneController {
    /**
     * In case of a pop-up screen, this is the stage of this scene.
     */
    public Stage myStage;
    
    /**
     * The application.
     */
    public ILuggageApplication app;
    
    /**
     * Gets called using reflection. Super classes need to extend this.
     * @param app The application
     */
    public SceneController(ILuggageApplication app) {
        this.app = app;
    }
    
    
    /**
     * Empty function to be optionally implemented by subclasses
     * Gets called when this scene gets changed/destroyed
     */
    public void onDestroy() {}
    
    /**
     * Empty function to be implemented by subclasses
     * Gets called when this scene gets initialised
     * @return A scene to be used for rendering
     */
    public abstract Scene onCreate();
}
