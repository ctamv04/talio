package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
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

        tasklists.setItems(FXCollections.observableArrayList(serverUtils.getTaskLists(boardId)));

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
                        setGraphic(ViewFactory.createTaskList(item.getId()).getValue());
                    }
                };
            }
        });

    }
}
