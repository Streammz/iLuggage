
package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.database.classes.Luggage;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;

/**
 *
 * @author robby
 */
public class CustomerListItem {
        
    public ILuggageApplication app;
    public Luggage myLuggage;
    public Parent root;

    
    @FXML private Text txtKind;
    @FXML private Text txtColor;
    @FXML private Text txtBrand;
    @FXML private Text txtSize;
    @FXML private Text txtStickers;
    @FXML private Text txtMisc;
    
    public void onCreate() {
        txtKind.setText(myLuggage.getKind(app).name);
        txtColor.setText(myLuggage.getColor(app).name);
        txtBrand.setText(myLuggage.getBrand(app).name);
        txtSize.setText(myLuggage.size);
        txtStickers.setText(myLuggage.stickers ? "Ja" : "Nee");
        txtMisc.setText(myLuggage.miscellaneous == null || myLuggage.miscellaneous.length() == 0 ? "-" : myLuggage.miscellaneous);
    }

}
