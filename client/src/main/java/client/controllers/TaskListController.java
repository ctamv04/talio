package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.TaskCard;
import models.TaskList;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Long id;

    public ListView<TaskCard> taskCards;

    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, long id) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.id = id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

            taskCards.setItems(FXCollections.observableArrayList(new TaskCard("adsa", new TaskList() )));


        taskCards.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TaskCard> call(ListView<TaskCard> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TaskCard item, boolean empty) {
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
