package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.controls.AutocompleteTextField;
import com.ithee.iluggage.core.database.classes.Luggage;
import com.ithee.iluggage.core.database.classes.LuggageKind;
import com.ithee.iluggage.core.scene.SubSceneController;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author iThee
 */
public class FoundLuggage extends SubSceneController {

    private static final String SQL_INSERT = "INSERT INTO `luggage` VALUES ("
            + "NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?, 1)";

    @FXML
    private ChoiceBox<LuggageKind> chKind;
    @FXML
    private AutocompleteTextField tfBrand;
    @FXML
    private AutocompleteTextField tfColor;
    @FXML
    private TextField tfSize1;
    @FXML
    private TextField tfSize2;
    @FXML
    private TextField tfSize3;
    @FXML
    private CheckBox cbStickers;
    @FXML
    private TextField tfFlightnr;
    @FXML
    private TextArea tfMisc;

    @Override
    public void onCreate() {
        chKind.getItems().add(null);
        app.dbKinds.getValues().forEach((kind) -> {
            chKind.getItems().add(kind);
        });

        app.dbBrands.getValues().forEach((brand) -> {
            tfBrand.getEntries().add(brand.name);
        });

        app.dbColors.getValues().forEach((color) -> {
            tfColor.getEntries().add(color.name);
        });
    }

    public void onCancel(ActionEvent event) {
        app.switchSubScene(null);
    }

    public void onAdd(ActionEvent event) {

        if (!isFormValid()) {
            app.showErrorMessage("form_invalid");
            return;
        }

        double[] sizes;
        try {
            sizes = getSizes();
        } catch (NumberFormatException ex) {
            return;
        }

        Luggage lugg = new Luggage();
        lugg.flightCode = tfFlightnr.getText();
        lugg.setKind(chKind.getValue());
        lugg.setBrand(app, tfBrand.getText());
        lugg.setColor(app, tfColor.getText());
        lugg.setSize(sizes);
        lugg.stickers = cbStickers.isSelected();
        lugg.miscellaneous = tfMisc.getText();
        lugg.date = new Date();

        lugg.id = app.db.executeStatement(SQL_INSERT, (statement) -> {
            statement.add(lugg.flightCode);
            statement.add(lugg.kind);
            statement.add(lugg.brand);
            statement.add(lugg.color);
            statement.add(lugg.size);
            statement.add(lugg.stickers);
            statement.add(lugg.miscellaneous);
            statement.add(lugg.date);
        });

        app.switchSubScene(null);
    }

    public boolean isFormValid() {
        if (this.chKind.getValue() == null) {
            return false;
        }

        try {
            getSizes();
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    private double[] getSizes() throws NumberFormatException {
        if (tfSize1.getLength() == 0 || tfSize2.getLength() == 0 || tfSize3.getLength() == 0) {
            return null;
        }
        return new double[]{
            Double.parseDouble(tfSize1.getText()),
            Double.parseDouble(tfSize2.getText()),
            Double.parseDouble(tfSize3.getText())
        };
    }
}
