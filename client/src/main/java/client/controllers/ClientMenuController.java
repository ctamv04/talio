package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Board;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final BoardController boardController;
    private final Board board;
    public Button copy_button;
    @FXML
    private Label boardTitle;
    @FXML
    private Button home_button;
    @FXML
    private Button editButton;

    @Inject
    public ClientMenuController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board, BoardController boardController) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
        this.boardController = boardController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void editBoard() {
        boardController.getOverlay().setVisible(true);
        mainCtrl.showEditBoardPage(board);
        boardController.getOverlay().setVisible(false);
    }

    public Label getBoardTitle() {return boardTitle;}

    public Button getHome_button() {
        return home_button;
    }

    public Button getCopy_button() {
        return copy_button;
    }
}
