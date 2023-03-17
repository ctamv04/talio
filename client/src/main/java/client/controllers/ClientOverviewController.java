package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientOverviewController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long boardId;

    @FXML
    public BorderPane layout;

    private MenuController menuController;
    private BoardController boardController;

    @Inject
    public ClientOverviewController(ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var menu= ViewFactory.createMenu();
        var board=ViewFactory.createBoard(boardId);

        layout.setTop(menu.getValue());
        layout.setCenter(board.getValue());
    }
}
