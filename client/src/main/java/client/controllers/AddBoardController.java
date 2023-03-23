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

public class AddBoardController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField board_name;
    @FXML
    private Button done_button;
    @FXML
    private Button back_button;

    @Inject
    public AddBoardController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        done_button.setOnMouseClicked(event -> save());
        back_button.setOnMouseClicked(event -> back());
    }

    public void back() {
        mainCtrl.closeAddBoard();
    }

    public void save() {
        String name = "Untitled";
        if (!board_name.getText().isBlank())
            name = board_name.getText();
        Board board = new Board(name);

        board = serverUtils.addBoard(board);
        back();
        mainCtrl.showClientOverview(serverUtils.getServer(), board.getId());
    }
}
