package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.database.classes.Customer;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.scene.SceneController;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.text.Text;

/**
 * @author iThee
 */
public class LuggageDetails extends SceneController {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    
    @FXML private Text txtType;
    @FXML private Text txtDate;
    @FXML private Text txtCustomer;
    
    @FXML private Text txtKind;
    @FXML private Text txtBrand;
    @FXML private Text txtStickers;
    @FXML private Text txtColor;
    @FXML private Text txtSize;
    
    @FXML private Text txtMisc;
    
    @Override
    public void onCreate() {
        
    }
    
    public void loadLuggage(Luggage luggage) {
        txtType.setText(luggage.status == 1 ? "Gevonden bagage" : luggage.status == 2  ? "Verloren bagage" : "Onbekend");
        txtDate.setText(dateFormat.format(luggage.date));
        if (luggage.customerId != 0) {
            Customer c = app.db.executeAndReadSingle(Customer.class, "SELECT * FROM `customer` WHERE `Id` = ?", luggage.customerId);
            txtCustomer.setText(c.name);
            txtCustomer.setCursor(Cursor.HAND);
            // TODO make clickable
        } else {
            txtCustomer.setText("-");
        }
        
        txtKind.setText(luggage.getKind(app).name);
        txtBrand.setText(luggage.getBrand(app).name);
        txtStickers.setText(luggage.stickers ? "Ja" : "Nee");
        txtColor.setText(luggage.getColor(app).name);
        txtSize.setText(luggage.size);
        
        txtMisc.setText(luggage.miscellaneous);
    }
    
    
    
}
