package client.controllers;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.TaskCard;

import java.net.URL;
import java.util.ResourceBundle;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskListId;

    @FXML
    public Label name;

    private StringProperty nameProperty;

    public ListView<TaskCard> taskCards;

    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, long taskListId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskListId = taskListId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //after pooling
//        nameProperty = new SimpleStringProperty();
//
//        name.textProperty().bind(nameProperty);


        taskCards.setItems(FXCollections.observableArrayList(serverUtils.getTaskCards(taskListId)));

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

        taskCards.setOnMouseClicked(event -> {
            mainCtrl.showCard((taskCards.getSelectionModel().getSelectedItem().getId()));
        });
    }
}
