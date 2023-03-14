package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long boardId;
    public Button home_button;

    @Inject
    public BoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        home_button.setOnAction(event -> mainCtrl.showStarting());
    }
}
