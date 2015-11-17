package com.ithee.iluggage;

import com.ithee.iluggage.core.database.DatabaseConnection;
import com.ithee.iluggage.core.database.classes.Account;
import com.ithee.iluggage.core.scene.PopupSceneController;
import com.ithee.iluggage.core.scene.SceneController;
import com.ithee.iluggage.screens.Login;
import com.ithee.iluggage.screens.MainMenu;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author robby
 */
public class ILuggageApplication extends Application {
    private DatabaseConnection db;
    
    private Stage primaryStage;
    private SceneController currentScene;
    
    private Account user;
    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setMinWidth(640);
        this.primaryStage.setMinHeight(480);
        //this.primaryStage.setMaximized(true);
        primaryStage.setTitle("Bagage (Corendon)");
        
        this.db = new DatabaseConnection(this);
        
        this.switchMainScene(Login.class);
    }
    
    public <T extends SceneController> T switchMainScene(Class<T> sceneClass) {
        T controller = initScene(sceneClass);
        
        if (this.currentScene != null) {
            this.currentScene.onDestroy();
        }
        
        controller.onCreate();
        this.primaryStage.setScene(controller.scene);
        this.currentScene = controller;
        
        if (!this.primaryStage.isShowing()) {
            this.primaryStage.show();
        }
        
        return controller;
    }
    
    public <T extends PopupSceneController> T openScene(Class<T> sceneClass) {
        T controller = initScene(sceneClass);
        
        controller.stage = new Stage();
        controller.onCreate();
        
        controller.stage.setScene(controller.scene);
        controller.stage.show();
        
        return controller;
    }
    
    private <T extends SceneController> T initScene(Class<T> sceneClass) {
        String fxmlName = "/" + sceneClass.getPackage().getName().replace('.', '/') + "/" + sceneClass.getSimpleName() + ".fxml";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlName));
            Parent root = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            
            controller.app = this;
            controller.scene = new Scene(root);
            
            return (T) controller;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public boolean tryLogin(String username, String password) {
        if (username == null || password == null) return false;
        long hash = password.hashCode();
        
        Account a = db.executeAndReadSingle(Account.class, "SELECT * FROM accounts WHERE `Username` = ? AND `Password` = ?", username, hash);
        if (a == null) {
            return false;
        } else {
            System.out.println("Logged in as userID=" + a.id);
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
    
    public boolean isUserManager() {
        return isUserLoggedIn() && this.user.permissionLevel >= 1;
    }
    
    public boolean isUserAdmin() {
        return isUserLoggedIn() && this.user.permissionLevel >= 2;
    }

    
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
