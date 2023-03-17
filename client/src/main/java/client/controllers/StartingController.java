package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Board;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartingController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    public ListView<Board> boards;
    public Button cardButton;

    @Inject
    public StartingController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boards.setItems(FXCollections.observableArrayList(serverUtils.getBoards()));

        boards.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Board> call(ListView<Board> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Board item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        setGraphic(new Label(item.getName()));
                    }
                };
            }
        });

        boards.setOnMouseClicked(event -> {
            mainCtrl.showBoard(boards.getSelectionModel().getSelectedItem());
        });

//        cardButton.setOnMouseClicked(event -> {
//            mainCtrl.showCard();
//        });
    }
}
