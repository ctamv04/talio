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
    private final BoardController boardController;
    private final Board board;
    @FXML
    private TextField board_title;
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

    /**
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Opens the board edit popup.
     */
    public void editBoard() {
        boardController.getOverlay().setVisible(true);
        mainCtrl.showEditBoardPage(board);
        boardController.getOverlay().setVisible(false);
    }


    public TextField getBoard_title() {
        return board_title;
    }

    public Button getHome_button() {
        return home_button;
    }
}
