package com.ithee.iluggage.screens;

import com.ithee.iluggage.core.scene.SceneController;
import com.ithee.iluggage.core.scene.SubSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

/**
 *
 * @author iThee
 */
public class MainMenu extends SceneController {

    @FXML
    private Text adminTitle, mngrTitle;
    @FXML
    private Text loggedinUser;

    @FXML
    private Button btnFoundLuggage, btnLostLuggage, btnSearchLuggage;
    @FXML
    private Button btnAddCustomer, btnSearchCustomer;
    @FXML
    private Button btnReport;
    @FXML
    private Button btnManageUsers;

    private Button currentSelected;

    @Override
    public void onCreate() {
        loggedinUser.setText(app.getUser().name);

        if (!app.isUserManager()) {
            removeNode(btnReport, mngrTitle, btnManageUsers, adminTitle);
        } else if (!app.isUserAdmin()) {
            removeNode(btnManageUsers, adminTitle);
        }
        
    }

    private <T extends SubSceneController> void clickButton(Class<T> theClass, Button btn) {
        if (this.currentSelected != null) {
            this.currentSelected.getStyleClass().remove("selected");

            if (this.currentSelected == btn) {
                this.currentSelected = null;
                app.switchSubScene(null);
                return;
            }
        }
        this.currentSelected = btn;
        btn.getStyleClass().add("selected");

        app.switchSubScene(theClass);
    }

    public void onPressFoundLuggage(ActionEvent event) {
        clickButton(FoundLuggage.class, btnFoundLuggage);
    }

    public void onPressLostLuggage(ActionEvent event) {
        clickButton(LostLuggage.class, btnLostLuggage);
    }

    public void onPressSearchLuggage(ActionEvent event) {
        clickButton(SearchLuggage.class, btnSearchLuggage);
    }

    public void onPressAddCustomer(ActionEvent event) {
        clickButton(CustomerAdd.class, btnAddCustomer);
    }

    public void onPressSearchCustomer(ActionEvent event) {
        clickButton(SearchCustomer.class, btnSearchCustomer);
    }

    public void onPressReport(ActionEvent event) {
        clickButton(Report.class, btnReport);
    }

    public void onPressManageUsers(ActionEvent event) {
        clickButton(ManageAccounts.class, btnManageUsers);
    }

    public void onPressLogout(ActionEvent event) {
        app.logOut();
        app.switchMainScene(Login.class);
    }

}
