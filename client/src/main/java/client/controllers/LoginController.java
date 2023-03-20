package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Button new_board_button;
    public Button join_board_button;
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    public LoginController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        join_board_button.setOnAction(event -> mainCtrl.showAddFirstBoardPage());
        new_board_button.setOnAction(event -> mainCtrl.showAddFirstBoardPage());
    }
}

