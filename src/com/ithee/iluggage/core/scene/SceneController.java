package com.ithee.iluggage.core.scene;

import com.ithee.iluggage.ILuggageApplication;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

/**
 * @author iThee
 */
public abstract class SceneController {

    /**
     * The application.
     */
    public ILuggageApplication app;

    /**
     * The scene that is used to contain the layout and information about it.
     */
    public Scene scene;

    /**
     * Override this in the superclass to have it execute a piece of code as
     * soon as the layout is shown
     */
    public void onCreate() {
    }

    /**
     * Helper function to remove a node from their parents. You can supply
     * multiple nodes at once.
     *
     * @param nodes
     */
    public void removeNode(Node... nodes) {
        for (Node node : nodes) {
            if (node.getParent() instanceof Pane) {
                Pane pane = (Pane) node.getParent();
                pane.getChildren().remove(node);
            }
        }
    }
    
    public void showSimpleMessage(AlertType type, String title, String content) {
        ILuggageApplication.showSimpleMessage(type, title, content);
    }
}
