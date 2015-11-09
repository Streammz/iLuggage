/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ithee.iluggage;

import com.ithee.iluggage.core.database.DatabaseConnection;
import com.ithee.iluggage.core.scene.PopupSceneController;
import com.ithee.iluggage.core.scene.SceneController;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author robby
 */
public class ILuggageApplication extends Application {
    private DatabaseConnection db;
    
    
    private Stage primaryStage;
    private SceneController currentScene;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Bagage (Corendon)");
    }
    
    public <T extends SceneController> void switchMainScene(Class<T> sceneClass) {
        SceneController scene = initScene(sceneClass);
        
        if (this.currentScene != null) {
            this.currentScene.onDestroy();
        }
        this.primaryStage.setScene(scene.onCreate());
        this.currentScene = scene;
        
        if (!this.primaryStage.isShowing()) {
            this.primaryStage.show();
        }
    }
    
    private <T extends SceneController> T initScene(Class<T> sceneClass) {
        try {
            SceneController controller = sceneClass.getConstructor().newInstance();
            controller.app = this;
            return (T) controller;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public <T extends PopupSceneController> void openScene(Class<T> sceneClass) {
        PopupSceneController scene = initScene(sceneClass);
        
        scene.myStage = new Stage();
        scene.myStage.setScene(scene.onCreate());
        scene.myStage.setTitle("????????");
        scene.myStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
