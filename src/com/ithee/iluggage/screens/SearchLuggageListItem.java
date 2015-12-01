package com.ithee.iluggage.screens;

import com.ithee.iluggage.ILuggageApplication;
import com.ithee.iluggage.core.database.classes.*;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.text.Text;

/**
 * @author iThee
 */
public class SearchLuggageListItem {

    public ILuggageApplication app;
    public Luggage myLuggage;
    public Parent root;
    public SearchLuggage parent;

    @FXML
    private Text txtKind;
    @FXML
    private Text txtColor;
    @FXML
    private Text txtBrand;
    @FXML
    private Text txtSize;
    @FXML
    private Text txtStickers;
    @FXML
    private Text txtMisc;

    public void onCreate() {
        LuggageKind kind = myLuggage.getKind(app);
        LuggageColor color = myLuggage.getColor(app);
        LuggageBrand brand = myLuggage.getBrand(app);

        txtKind.setText(kind == null ? "-" : kind.name);
        txtColor.setText(color == null ? "-" : color.name);
        txtBrand.setText(brand == null ? "-" : brand.name);
        txtSize.setText(myLuggage.size == null ? "-" : myLuggage.size);
        txtStickers.setText(myLuggage.stickers ? "Ja" : "Nee");
        txtMisc.setText(myLuggage.miscellaneous == null || myLuggage.miscellaneous.length() == 0 ? "-" : myLuggage.miscellaneous);
    }

    public void onClick() {
        app.showPopupScene(LuggageDetails.class).loadLuggage(myLuggage, () -> parent.onSearch());
    }

    public void onClickDelete() {
        boolean delete = ILuggageApplication.showConfirmDialog("Bagage verwijderen",
                "Weet je zeker dat je deze bagage wilt verwijderen?");

        if (delete) {
            app.db.executeStatement("DELETE FROM `luggage` WHERE `Id` = ?", myLuggage.id);
            // Refresh the results
            parent.onSearch();
        }
    }

}
