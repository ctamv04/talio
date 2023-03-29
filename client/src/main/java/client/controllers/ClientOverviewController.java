package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import models.Board;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientOverviewController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Board board;
    private BoardController boardController;
    @FXML
    public BorderPane layout;

    @Inject
    public ClientOverviewController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var boardPair = mainCtrl.createBoard(board);
        boardController = boardPair.getKey();
        var menuPair = mainCtrl.createClientMenu(board, boardController);

        layout.setTop(menuPair.getValue());
        layout.setCenter(boardPair.getValue());

        ClientMenuController clientMenuController = menuPair.getKey();

        clientMenuController.getBoard_title().textProperty().bind(Bindings.concat("Talio | ",
                boardController.namePropertyProperty(), " (#", board.getId(), ")"));
        clientMenuController.getHome_button().setOnAction(event -> {
            boardController.closePolling();
            mainCtrl.showLoginPage();
        });
    }

    public void closePolling() {
        boardController.closePolling();
    }
}
