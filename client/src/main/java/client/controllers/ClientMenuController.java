package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.Board;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Board board;
    @FXML
    private TextField board_title;
    @FXML
    private Button home_button;
    @FXML
    private Button editButton;

    @Inject
    public ClientMenuController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void editBoard() {
        mainCtrl.showEditBoardPage(board);
    }


    public TextField getBoard_title() {
        return board_title;
    }

    public Button getHome_button() {
        return home_button;
    }
}
