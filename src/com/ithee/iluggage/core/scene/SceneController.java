
package com.ithee.iluggage.core.scene;

import com.ithee.iluggage.ILuggageApplication;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author robby
 */
public abstract class SceneController {
    /**
     * The application.
     */
    public ILuggageApplication app;
    
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
