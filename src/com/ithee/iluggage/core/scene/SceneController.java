
package com.ithee.iluggage.core.scene;

import com.ithee.iluggage.ILuggageApplication;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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
     * The scene
     */
    public Scene scene;
    
    
    public void removeNode(Node... nodes) {
        for (Node node : nodes) {
            if (node.getParent() instanceof Pane) {
                Pane pane = (Pane) node.getParent();
                pane.getChildren().remove(node);
            }
        }
    }
}
