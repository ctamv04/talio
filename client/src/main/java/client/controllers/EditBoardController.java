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

public class EditBoardController implements Initializable{
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Board board;
    @FXML
    private TextField board_name;
    @FXML
    private Button done_button;
    @FXML
    private Button back_button;

    @Inject
    public EditBoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void back() {
        mainCtrl.closeEditBoard();
    }

    public void save() {
        String name = board.getName();
        if (!board_name.getText().isBlank())
            name = board_name.getText();
        board.setName(name);

        serverUtils.updateBoard(board.getId(), board);

        back();
    }
}
