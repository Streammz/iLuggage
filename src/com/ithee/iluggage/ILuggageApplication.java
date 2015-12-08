package com.ithee.iluggage;

import com.ithee.iluggage.core.database.AddableDatabaseCache;
import com.ithee.iluggage.core.database.DatabaseCache;
import com.ithee.iluggage.core.database.DatabaseConnection;
import com.ithee.iluggage.core.database.classes.Account;
import com.ithee.iluggage.core.database.classes.LuggageBrand;
import com.ithee.iluggage.core.database.classes.LuggageColor;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SubSceneController;
import com.ithee.iluggage.core.scene.SceneController;
import com.ithee.iluggage.core.security.PasswordHasher;
import com.ithee.iluggage.screens.Login;
import com.ithee.iluggage.screens.MainMenu;
import com.ithee.iluggage.screens.WelcomeScreen;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * De singleton-class van het project. In deze class zit de functie die als
 * eerste word aangeroepen als de applicatie word opgestart, en tevens alle
 * methodes en referenties naar belangerijke objecten zoals de database.
 *
 * @author iThee
 */
public class ILuggageApplication extends Application {

    /**
     * De minimum breedte/hoogte van de applicatie.
     */
    private static final int MIN_WIDTH = 1275, MIN_HEIGHT = 750;

    /**
     * De database helper-class, waar de verbinding met de database tot stand
     * word gebracht.
     */
    public DatabaseConnection db;

    /**
     * Een cache voor bagagesoorten.
     */
    public List<LuggageKind> dbKinds;

    /**
     * Een cache voor bagagemerken. Aan deze cache kan worden toegevoegd.
     */
    public AddableDatabaseCache<LuggageBrand> dbBrands;

    /**
     * Een cache voor kleuren. Aan deze cache kan worden toegevoegd.
     */
    public AddableDatabaseCache<LuggageColor> dbColors;

    /**
     * De hoofd stage van de applicatie. Dit is de stage waar het huidige scene
     * in staat.
     */
    private Stage primaryStage;

    /**
     * De huidige scene van de applicatie. Dit is de scene waar het hoofdscherm
     * in staat.
     */
    private SceneController currentScene;

    /**
     * De huidig ingelogde gebruiker. Deze kan worden aangepast door middel van
     * de tryLogin functie, en worden opgehaald doormiddel van de getUser
     * functie.
     */
    private Account user;

    /**
     * De resources waar de taal in word opgeslagen en van geladen.
     */
    private ResourceBundle language;
    
    /**
     * De taal val de geselecteerde language.
     */
    private String languageCountry;

    /**
     * De eerste functie die word aangeroepen zodra de applicatie word
     * opgestart. Hier komen alle initialisatie processen in.
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        // Laad alle zelf-toegevoegde fonts en voeg deze toe aan het programma.
        Font.loadFont(ILuggageApplication.class.getResource("/Uni-Sans-Bold.otf").toExternalForm(), 10);
        Font.loadFont(ILuggageApplication.class.getResource("/Uni-Sans-HeavyItalic.otf").toExternalForm(), 10);

        // Laad de standaard taal
        setLanguage("nl", "NL");

        // Initializeer het scherm en zijn standaardwaarden.
        this.primaryStage = primaryStage;
        this.primaryStage.getIcons().add(new Image("/Appicon.png"));
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);
        this.primaryStage.setWidth(MIN_WIDTH);
        this.primaryStage.setHeight(MIN_HEIGHT);
        primaryStage.setTitle("iLuggage | Corendon");

        // Initializeer de database en gecachde objecten
        this.db = new DatabaseConnection(this);
        this.dbKinds = new ArrayList<>();
        for (int i=1; i<=4; i++) {
            dbKinds.add(new LuggageKind(this, i));
        }
        this.dbBrands = new AddableDatabaseCache<LuggageBrand>(this, "SELECT * FROM `luggagebrands`",
                "INSERT INTO `luggagebrands` VALUES (NULL, ?)", LuggageBrand.class) {

                    @Override
                    public Object[] getDbParams(LuggageBrand brand) {
                        return new Object[]{brand.name};
                    }
                };
        this.dbColors = new AddableDatabaseCache<LuggageColor>(this, "SELECT * FROM `luggagecolors`",
                "INSERT INTO `luggagecolors` VALUES (NULL, ?)", LuggageColor.class) {

                    @Override
                    public Object[] getDbParams(LuggageColor color) {
                        return new Object[]{color.name};
                    }
                };

        // Schakel naar het eerste scherm: het inlogscherm.
        this.switchMainScene(Login.class);
    }

    /**
     * Schakelt het hoofdscherm naar de meegegeven controller/view. Het
     * hoofdscherm word in principe alleen gebruikt voor het inlogscherm en het
     * scherm waar het hoofdmenu in komt.
     *
     * @param <T>
     * @param sceneClass De controller class die gebruikt word. Dit moet een
     * subclass zijn van SceneController.
     * @return De referentie naar de aangeroepen controller. Deze is al
     * geïnitialiseert.
     */
    public <T extends SceneController> T switchMainScene(Class<T> sceneClass) {
        // Maakt een controller aan en initialiseert deze
        T controller = initScene(sceneClass);
        controller.onCreate();

        // Veranderd de Scene van de main stage naar een nieuwe scene met de 
        // aangemaakte view.
        controller.stage = this.primaryStage;
        this.primaryStage.setScene(new Scene(controller.root, MIN_WIDTH, MIN_HEIGHT));
        this.currentScene = controller;

        // Verander de kleuren van de applicatie aan de hand van de ingelogde 
        // gebruiker
        updateColors();

        // Laat het scherm zien indien deze nog niet weergegeven word
        if (!this.primaryStage.isShowing()) {
            this.primaryStage.show();
        }

        if (controller instanceof MainMenu) {
            switchSubScene(WelcomeScreen.class);
        }

        // Geeft de controller mee terug.
        return controller;
    }

    /**
     * Opent een nieuw scherm met de meegegeven controller/view. Een scherm
     * zoals deze kan zichzelf sluiten, of laten worden afgehandeld door de
     * gebruiker zelf. Pop-up schermen moeten worden gebruikt indien er extra
     * informatie moet worden getoont zonder de gebruiker van zijn huidige
     * scherm af te halen.
     *
     * @param <T>
     * @param sceneClass De controller class die gebruikt word. Dit moet een
     * subclass zijn van SceneController.
     * @return De referentie naar de aangeroepen controller. Deze is al
     * geïnitialiseert.
     */
    public <T extends SceneController> T showPopupScene(Class<T> sceneClass) {
        // Maakt een controller aan en initialiseert deze
        T controller = initScene(sceneClass);
        controller.onCreate();

        // Maakt een nieuwe Stage aan met standaardwaarden en laat deze zien.
        Stage stage = new Stage();
        controller.stage = stage;
        stage.getIcons().add(new Image("/Appicon.png"));
        stage.setTitle("iLuggage | Corendon");
        stage.setScene(new Scene(controller.root));
        stage.show();

        // Geeft de controller mee terug.
        return controller;
    }

    /**
     * Opent een scherm binnen een andere scherm met de meegegeven
     * controller/view. Het scherm word alleen veranderd als het hoofdscherm een
     * BorderedPane is. Als de applicatie normaal gebruikt word, dan is dit
     * altijd het geval tenzij je op het inlogscherm zit. Deze functie is
     * gemaakt zodat het hoofdmenu altijd aan de zeikant blijft staan zonder het
     * scherm altijd op nieuw te verversen.
     *
     * @param <T>
     * @param sceneClass De controller class die gebruikt word. Dit moet een
     * subclass zijn van SubSceneController.
     * @return De referentie naar de aangeroepen controller. Deze is al
     * geïnitialiseert.
     */
    public <T extends SubSceneController> T switchSubScene(Class<T> sceneClass) {
        // Indien de huidige scene geen root heeft van BorderedPane, dan 
        // returnt de functie null.
        if (this.currentScene.root instanceof BorderPane) {
            BorderPane currentPane = (BorderPane) this.currentScene.root;

            // Indien de meegegeven class null is, dan word een leeg scherm 
            // getoont. Anders word de meegegeven class aangemaakt.
            if (sceneClass == null) {
                ((MainMenu)currentScene).subscene = null;
                currentPane.setCenter(null);
                return null;
            } else {
                // Maakt een controller aan en initialiseert deze
                T controller = initScene(sceneClass);
                controller.onCreate();
                controller.stage = this.currentScene.stage;
                ((MainMenu)currentScene).subscene = controller;

                // Veranderd het center gedeelte van de BorderedPane naar de 
                // aangemaakte View van de controller.
                currentPane.setCenter(controller.root);

                // Geeft de controller mee terug.
                return controller;
            }
        } else {
            return null;
        }
    }

    /**
     * Initialiseert een controller+view met de meegegeven controller class.
     * Deze laadt automatisch de bijhorende FXML bestand en initialiseert de
     * controller en view voor gebruik. De view word nog nergens op weergegeven,
     * maar is wel klaar voor gebruik.
     *
     * @param <T>
     * @param sceneClass De controller class die gebruikt word. Dit moet een
     * subclass zijn van SceneController.
     * @return De geinitialiseerde controller. De view is al aangemaakt als node
     * maar nog nergens weergegeven.
     */
    private <T extends SceneController> T initScene(Class<T> sceneClass) {
        // Genereert de naam van de .fxml bestand aan de hand van de gegevens 
        // van de meegegeven class. Dit is bijvoorbeeld:
        //   /com/ithee/iluggage/screens/Login.fxml
        String fxmlName = "/" + sceneClass.getPackage().getName().replace('.', '/') + "/" + sceneClass.getSimpleName() + ".fxml";
        try {
            // Laad de view en controller door middel van het fxml bestand.
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlName));
            fxmlLoader.setResources(language);
            Parent root = fxmlLoader.load();
            T controller = fxmlLoader.getController();

            // Vult de standaardreferenties aan van de controller zodat deze 
            // makkelijker benaderbaar zijn.
            controller.app = this;
            controller.root = root;

            return (T) controller;
        } catch (Exception ex) {
            // Een error hoort hier niet te ontstaan in een productieapplicatie,
            // enkel tijdens het ontwikkelen. Laat hier om de applicatie crashen
            // en de correcte foutmelding weergeven. 
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Geeft het huidig taalbestand mee terug.
     * @return Het huidig taalbestand.
     */
    public ResourceBundle getLanguage() {
        return this.language;
    }
    
    public String getCountry() {
        return this.languageCountry;
    }
    
    public void setLanguage(String language, String country) {
        this.language = ResourceBundle.getBundle("language", new Locale(language, country));
        this.languageCountry = language;
        
        if (primaryStage != null && primaryStage.isShowing()) {
            if (currentScene instanceof MainMenu) {
                if (showConfirmDialog("change_language")) {
                    switchMainScene(MainMenu.class);
                }
            }
        }
    }

    /**
     * Probeert in te loggen met het meegegeven gebruikersnaam en wachtwoord.
     * Het wachtwoord word automatisch gehasht. De gegevens van de ingelogde
     * gebruiker zijn op te halen via verschillende methodes en velden in deze
     * class.
     *
     * @param username Het gebruikersnaam waarmee ingelogd moet worden.
     * @param password Het (ongehashde) wachtwoord waarmee ingelogd moet worden.
     * @return True als met de meegegeven parameters is ingelogd, en anders
     * False.
     */
    public boolean tryLogin(String username, String password) {
        // Indien de  gebruikersnaam of wachtwoord ontbreekt, geef false terug.
        if (username == null || password == null) {
            return false;
        }

        // Laad het accountinformatie aan de hand van de gebruikersnaam.
        Account a = db.executeAndReadSingle(Account.class, "SELECT * FROM `accounts` WHERE `Username` = ?", username);

        // Indien het account niet bestaat, geef false terug.
        if (a == null) {
            return false;
        }

        // Indien het salt niet bestaat (oudere versie van de database), 
        // genereer er hier een. 
        if (a.salt == null || a.salt.length() < 8) {
            a.salt = PasswordHasher.generateSalt();
            db.executeStatement("UPDATE `accounts` SET `Salt` = ? WHERE `Id` = ?", a.salt, a.id);
        }

        // Pak de salt en voeg deze toe aan het wachtwoord, en maak er dan een 
        // hash van.
        String hash = PasswordHasher.generateHash(password, a.salt);

        //System.out.println("Logging in with username " + username + " & pass " + hash);
        // Controleer of het gegenereerde hash klopt met de bestaande hash.
        if (!a.password.equals(hash)) {
            return false;
        } else {
            // Verander de laatste inlogtijd naar het huidige tijdstip.
            a.lastLogin = new Date();
            db.executeStatement("UPDATE `accounts` SET `LastLogin` = ? WHERE `Id` = ?", a.lastLogin, a.id);

            // Verander de huidig ingelogde gebruiker in de applicatie naar deze.
            this.user = a;

            // Werk de kleuren van de applicatie bij.
            updateColors();

            return true;
        }
    }

    /**
     * Logt de gebruiker uit van de applicatie.
     */
    public void logOut() {
        if (isUserLoggedIn()) {
            this.user = null;
        }
    }

    /**
     * Geeft true aan indien de gebruiker is ingelogd.
     *
     * @return True als de gebruiker is ingelogd, anders false.
     */
    public boolean isUserLoggedIn() {
        return (this.user != null);
    }

    /**
     * Geeft de ingelogde gebruiker terug. Indien de gebruiker niet is ingelogd,
     * dan geeft dit null terug.
     *
     * @return De ingelogde gebruiker.
     */
    public Account getUser() {
        return this.user;
    }

    /**
     * Geeft true aan indien de ingelogde gebruiker een manager is.
     *
     * @return True als de ingelogde gebruiker een manager is, anders false.
     */
    public boolean isUserManager() {
        return isUserLoggedIn() && this.user.permissionLevel >= 1;
    }

    /**
     * Geeft true aan indien de ingelogde gebruiker een administrator is.
     *
     * @return True als de ingelogde gebruiker een administrator is, anders
     * false.
     */
    public boolean isUserAdmin() {
        return isUserLoggedIn() && this.user.permissionLevel >= 2;
    }

    /**
     * Werkt de hoofdkleuren van de applicatie bij aan de hand van de ingelogde
     * gebruiker. Voor een medewerker is dit blauw, voor een manager rood, en
     * voor een administrator oranje.
     */
    public void updateColors() {
        if (this.currentScene == null) {
            return;
        }

        this.currentScene.root.getStyleClass().removeAll("colored1", "colored2");

        if (isUserAdmin()) {
            this.currentScene.root.getStyleClass().add("colored2");
        } else if (isUserManager()) {
            this.currentScene.root.getStyleClass().add("colored1");
        }
    }

    /**
     * Haalt een (vertaalde) string op aan de hand van de meegegeven key. De
     * string komt uit de language-bundle.
     *
     * @param key De key om op te halen
     * @return Een (vertaalde) string die bij de key hoort.
     */
    public String getString(String key) {
        return language.getString(key);
    }

    /**
     * Laat een simpele dialoog zien met het meegegeven type, titel en bericht.
     *
     * @param type Het type wat het dialoog moet zijn.
     * @param title De titel van het dialoog.
     * @param content Het bericht wat in het dialoog staat.
     */
    public static void showSimpleMessage(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Laat een error message zien met een bericht uit het talenbestand.
     *
     * @param errorKey De naam van de key in het talenbestand.
     * @param params De parameters die vervangen worden in het bericht.
     */
    public void showErrorMessage(String errorKey, Object... params) {
        String title = getString("error_" + errorKey + "_title");
        String message = getString("error_" + errorKey);
        if (params != null && params.length > 0) {
            message = String.format(message, params);
        }
        showSimpleMessage(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Laat een confirmatie dialoog zien met het meegegeven titel en bericht.
     * Deze worden opgehaald uit het talenbestand.
     *
     * @param key De naam van de key in het talenbestand.
     * @param params De parameters die vervangen worden in het bericht.
     * @return True als de gebruiker op OK heeft gedrukt, en anders false.
     */
    public boolean showConfirmDialog(String key, Object... params) {
        String title = getString("dialog_" + key + "_title");
        String message = getString("dialog_" + key);
        if (params != null && params.length > 0) {
            message = String.format(message, params);
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.isPresent() && result.get() == ButtonType.OK);
    }

    /**
     * Standaard JavaFX functie die word aangeroepen indien er gebruikt word
     * gemaakt van een IDE zonder JavaFX-support.
     *
     * @param args De meegegeven argumenten van de applicatie
     */
    public static void main(String[] args) {
        launch(args);
    }

}
