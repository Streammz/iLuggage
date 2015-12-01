
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.*;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author iThee
 */
public class ManageAccounts extends SubSceneController {
    private static final String DEFAULT_QUERY = "SELECT * FROM `accounts`";
    private static final int MAX_DISPLAY_SIZE = 25;

    @FXML private TextField tfName;
    
    @FXML private VBox results;
    
    @Override
    public void onCreate() {
        List<Account> list = app.db.executeAndReadList(Account.class, DEFAULT_QUERY);
        showResults(list);
    }
    
    @FXML public void onSearch() {
        List<String> wheres = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        if (tfName.getLength() > 0) {
            wheres.add("`Name` LIKE ?");
            params.add("%" + tfName.getText() + "%");
        }
        
        String query = DEFAULT_QUERY;
        if (wheres.size() > 0) {
            query += " WHERE ";
            for (int i=0; i<wheres.size(); i++) {
                query = query + wheres.get(i) + " AND ";
            }
            query = query.substring(0, query.length()-5);
        }
        
        List<Account> list = app.db.executeAndReadList(Account.class, query, params.toArray());
        showResults(list);
    }
    
    public void showResults(List<Account> list) {
        // Clear old list (if there is anything in it)
        this.results.getChildren().clear();
        
        int size = Math.min(list.size(), MAX_DISPLAY_SIZE);
        for (int i=0; i<size; i++) {
            Account account = list.get(i);
            try {
                //Load a new item (ManageAccountsListItem.java) so that it can be displayed in the list
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/ithee/iluggage/screens/ManageAccountsListItem.fxml"));
                Parent parent = loader.load();
                ManageAccountsListItem controller = loader.getController();
                controller.myAccount = account;
                controller.root = parent;
                controller.app = this.app;
                controller.parent = this;
                controller.onCreate();
                
                // Add to the list and style
                this.results.getChildren().add(controller.root);
                
                // Add a seperator between items
                this.results.getChildren().add(new Separator(Orientation.HORIZONTAL));
            } catch (Exception ex) {
                // This should not happen in regular usage. if it DOES happen, its a coding error.
                throw new RuntimeException(ex);
            }
        }
    }
    
    public void onClickAdd() {
        app.showPopupScene(AccountAdd.class);
    }

    
    
}
