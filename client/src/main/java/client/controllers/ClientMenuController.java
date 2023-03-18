package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button home_button;

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    public ClientMenuController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        home_button.setOnAction(event -> mainCtrl.showStarting());
    }
}
