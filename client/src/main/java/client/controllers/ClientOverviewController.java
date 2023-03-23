package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientOverviewController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long boardId;
    private BoardController boardController;
    @FXML
    public BorderPane layout;

    @Inject
    public ClientOverviewController(ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var menu = mainCtrl.createClientMenu();
        var board = mainCtrl.createBoard(boardId);

        layout.setTop(menu.getValue());
        layout.setCenter(board.getValue());

        ClientMenuController clientMenuController = menu.getKey();
        boardController=board.getKey();

        clientMenuController.getBoard_title().textProperty().bind(Bindings.concat("Talio | ",
                boardController.namePropertyProperty()," (#",boardId,")"));
        clientMenuController.getHome_button().setOnAction(event -> {
            boardController.closePolling();
            mainCtrl.showLoginPage();
        });
    }

    public void closePolling(){
        boardController.closePolling();
    }
}
