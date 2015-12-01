package com.ithee.iluggage.core.scene;

import com.ithee.iluggage.ILuggageApplication;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Een controller waarvan de view automatisch geladen kan worden. Deze class
 * bevat tevens enkele helperfuncties voor veelgebruikte functies in
 * controllers.
 *
 * @author iThee
 */
public abstract class SceneController {

    /**
     * Een referentie naar de applicatie.
     */
    public ILuggageApplication app;

    /**
     * De root node, oftewel de node waar alle views in verwerkt zitten.
     */
    public Parent root;

    /**
     * De stage waarin de view word weergegeven van de controller.
     */
    public Stage stage;

    /**
     * Override deze functie om de superclass een functie uit te laten voeren
     * zodra het layout word weergegeven.
     */
    public void onCreate() {
    }

    /**
     * Helper functie om één of meerdere nodes te verwijderen van zijn parent.
     *
     * @param nodes De nodes om te verwijderen.
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
     * Laat een simpele dialoog zien met het meegegeven type, titel en bericht.
     *
     * @param type Het type wat het dialoog moet zijn.
     * @param title De titel van het dialoog.
     * @param content Het bericht wat in het dialoog staat.
     */
    public void showSimpleMessage(AlertType type, String title, String content) {
        ILuggageApplication.showSimpleMessage(type, title, content);
    }
    
    /**
     * Laat een confirmatie dialoog zien met het meegegeven titel en bericht.
     *
     * @param title De titel van het dialoog.
     * @param content Het bericht wat in het dialoog staat.
     * @return True als de gebruiker op OK heeft gedrukt, en anders false.
     */
    public boolean showConfirmDialog(String title, String content) {
        return ILuggageApplication.showConfirmDialog(title, content);
    }
}
