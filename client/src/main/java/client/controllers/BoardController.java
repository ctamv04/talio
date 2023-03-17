package client.controllers;

import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Board;
import models.TaskList;

import java.net.URL;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long boardId;
    public Button home_button;
    public ListView<TaskList> tasklists;


    @Inject
    public BoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            tasklists.setItems(FXCollections.observableArrayList(serverUtils.getTasklists()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        tasklists.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TaskList> call(ListView<TaskList> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TaskList item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item==null||empty){
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        setGraphic(new Label(item.getName()));
                    }
                };
            }
        });

    }
}
