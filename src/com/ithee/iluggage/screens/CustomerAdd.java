
package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.PopupSceneController;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class CustomerAdd extends PopupSceneController {
    
    private static final String SQL_INSERT_CUSTOMER = "INSERT INTO `customers` VALUES ("
            + "NULL,  ?, ?, ?)";
    

    @FXML private TextField tfCustomername;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPhone;
    private String name;
    private String phone;
    private String email;

   
    public void onCancel(ActionEvent event) {
        this.stage.close();
    }

    public void onAdd(ActionEvent event) {
        
  
        CustomerAdd cus = new CustomerAdd();
        cus.name = tfCustomername.getText();
        cus.email = tfEmail.getText();
        cus.phone = tfPhone.getText();
        
        app.db.executeStatement(SQL_INSERT_CUSTOMER, 
                cus.name, cus.email, cus.phone);
        

       
        this.stage.close();
    }
    

        
    
}
