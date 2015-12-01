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
import java.util.Date;
import java.util.Optional;
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
 *
 * @author iThee
 */
public class ILuggageApplication extends Application {
    
    private static final int MIN_WIDTH = 1200, MIN_HEIGHT = 900;
    
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
        this.primaryStage.getIcons().add(new Image("/Appicon.png"));
        this.primaryStage.setMaximized(true);
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);
        this.primaryStage.setWidth(MIN_WIDTH);
        this.primaryStage.setHeight(MIN_HEIGHT);
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
        
        controller.stage = this.primaryStage;
        this.primaryStage.setScene(new Scene(controller.root, MIN_WIDTH, MIN_HEIGHT));
        this.currentScene = controller;
        
        updateColors();
        if (!this.primaryStage.isShowing()) {
            this.primaryStage.show();
        }
        
        return controller;
    }
    
    public <T extends SceneController> T showPopupScene(Class<T> sceneClass) {
        T controller = initScene(sceneClass);
        controller.onCreate();
        
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/Appicon.png"));
        controller.stage = stage;
        stage.setTitle("iLuggage | Corendon");
        stage.setScene(new Scene(controller.root));
        stage.show();
        
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
                controller.stage = this.currentScene.stage;

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
        
        Account a = db.executeAndReadSingle(Account.class, "SELECT * FROM `accounts` WHERE `Username` = ?", username);
        if (a == null) {
            return false;
        }
        if (a.salt == null || a.salt.length() < 8) {
            a.salt = PasswordHasher.generateSalt();
            db.executeStatement("UPDATE `accounts` SET `Salt` = ? WHERE `Id` = ?", a.salt, a.id);
        }
        
        String hash = PasswordHasher.generateHash(a.salt + password);
        System.out.println("Logging in with username " + username + " & pass " + hash);
        
        a = db.executeAndReadSingle(Account.class, "SELECT * FROM `accounts` WHERE `Username` = ? AND `Password` = ?", username, hash);
        if (a == null) {
            return false;
        } else {
            a.lastLogin = new Date();
            db.executeStatement("UPDATE `accounts` SET `LastLogin` = ? WHERE `Id` = ?", a.lastLogin, a.id);
            
            this.user = a;
            updateColors();
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
    
    public void updateColors() {
        if (this.currentScene == null) return;
        
        this.currentScene.root.getStyleClass().removeAll("colored1", "colored2");
        
        if (isUserAdmin()) {
            this.currentScene.root.getStyleClass().add("colored2");
        } else if (isUserManager()) {
            this.currentScene.root.getStyleClass().add("colored1");
        }
    }

    
    
    public static void showSimpleMessage(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
    
    public static boolean showConfirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return (result.isPresent() && result.get() == ButtonType.OK);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
