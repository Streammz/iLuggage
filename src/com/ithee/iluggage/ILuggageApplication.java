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
import java.security.MessageDigest;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author iThee
 */
public class ILuggageApplication extends Application {
    
    private static final int MAX_WIDTH = 1100, MAX_HEIGHT = 800;
    
    public DatabaseConnection db;
    
    private Stage primaryStage;
    private SceneController currentScene;
    
    private Account user;
    
    public DatabaseCache<LuggageKind> dbKinds;
    public AddableDatabaseCache<LuggageBrand> dbBrands;
    public AddableDatabaseCache<LuggageColor> dbColors;
            
    
    
    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(ILuggageApplication.class.getResource("/Uni-Sans-Bold.otf").toExternalForm(), 10);
        
        this.primaryStage = primaryStage;
        this.primaryStage.setMinWidth(MAX_WIDTH);
        this.primaryStage.setMinHeight(MAX_HEIGHT);
        primaryStage.setTitle("iLuggage | Corendon");
        
        this.db = new DatabaseConnection(this);
        this.dbKinds = new DatabaseCache(this, "SELECT * FROM `luggagekinds`", LuggageKind.class);
        this.dbBrands = new AddableDatabaseCache<LuggageBrand>(this, "SELECT * FROM `luggagebrands`",
                "INSERT INTO `luggagebrands` VALUES (NULL, ?)", LuggageBrand.class) {
                    
            @Override public Object[] getDbParams(LuggageBrand brand) {
                return new Object[] { brand.name };
            }
        };
        this.dbColors = new AddableDatabaseCache<LuggageColor>(this, "SELECT * FROM `luggagecolors`",
                "INSERT INTO `luggagecolors` VALUES (NULL, ?)", LuggageColor.class) {
                    
            @Override public Object[] getDbParams(LuggageColor color) {
                return new Object[] { color.name };
            }
        };
        
        if (tryLogin("admin", "admin")) {
            this.switchMainScene(MainMenu.class);
        } else {
            this.switchMainScene(Login.class);
        }
    }
    
    public <T extends SceneController> T switchMainScene(Class<T> sceneClass) {
        T controller = initScene(sceneClass);
        
        controller.onCreate();
        
        this.primaryStage.setScene(new Scene(controller.root, MAX_WIDTH, MAX_HEIGHT));
        this.currentScene = controller;
        
        if (!this.primaryStage.isShowing()) {
            this.primaryStage.show();
        }
        
        return controller;
    }
    
    public <T extends SubSceneController> T switchSubScene(Class<T> sceneClass) {
        if (this.currentScene.root instanceof BorderPane) {
            BorderPane currentPane = (BorderPane)this.currentScene.root;
            
            if (sceneClass == null) {
                currentPane.setCenter(null);
                return null;
            } else {
                T controller = initScene(sceneClass);
                controller.onCreate();

                currentPane.setCenter(controller.root);
                return controller;
            }
        } else {
            return null;
        }
    }
    
    private <T extends SceneController> T initScene(Class<T> sceneClass) {
        String fxmlName = "/" + sceneClass.getPackage().getName().replace('.', '/') + "/" + sceneClass.getSimpleName() + ".fxml";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlName));
            Parent root = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            
            controller.app = this;
            controller.root = root;
            
            return (T) controller;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public boolean tryLogin(String username, String password) {
        if (username == null || password == null) return false;
        String hash = PasswordHasher.generateHash(password);
        
        System.out.println("Log in with username " + username + " & pass " + hash);
        
        Account a = db.executeAndReadSingle(Account.class, "SELECT * FROM `accounts` WHERE `Username` = ? AND `Password` = ?", username, hash);
        if (a == null) {
            return false;
        } else {
            System.out.println("Logged in as userID=" + a.id);
            this.user = a;
            return true;
        }
    }
    
    public void logOut() {
        if (isUserLoggedIn()) {
            this.user = null;
        }
    }
    
    public boolean isUserLoggedIn() {
        return (this.user != null);
    }
    
    public Account getUser() {
        return this.user;
    }
    
    public boolean isUserManager() {
        return isUserLoggedIn() && this.user.permissionLevel >= 1;
    }
    
    public boolean isUserAdmin() {
        return isUserLoggedIn() && this.user.permissionLevel >= 2;
    }

    
    
    public static void showSimpleMessage(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);

        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
