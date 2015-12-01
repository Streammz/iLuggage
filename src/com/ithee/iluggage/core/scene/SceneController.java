package com.ithee.iluggage.core.scene;

import com.ithee.iluggage.ILuggageApplication;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author iThee
 */
public abstract class SceneController {

    /**
     * The application.
     */
    public ILuggageApplication app;

    /**
     * The root node.
     */
    public Parent root;
    
    /**
     * The stage this controller is displayed in
     */
    public Stage stage;

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
     * @param nodes The nodes to remove
     */
    public void removeNode(Node... nodes) {
        for (Node node : nodes) {
            if (node.getParent() instanceof Pane) {
                Pane pane = (Pane) node.getParent();
                pane.getChildren().remove(node);
            }
        }
    }

    /**
     * Shows an alert and waits for it to dissapear before returning.
     *
     * @param type The type of alert to display, like an error or information
     * dialog.
     * @param title The title of the alert.
     * @param content The message of the alert.
     */
    public void showSimpleMessage(AlertType type, String title, String content) {
        ILuggageApplication.showSimpleMessage(type, title, content);
    }
}
