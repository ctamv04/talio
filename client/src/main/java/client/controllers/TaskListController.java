package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.TaskCard;
import models.TaskList;

import javax.swing.text.View;
import java.net.URL;
import java.util.*;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskListId;
    private Map<Long, Parent> cache;
    private Timer timer;
    @FXML
    private Label taskList_name;
    @FXML
    public ListView<TaskCard> taskCards;
    private final List<MinimizedCardController> taskCardControllers=new ArrayList<>();

    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, long taskListId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskListId = taskListId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cache=new HashMap<>();
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        },0,500);
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
                        if(!cache.containsKey(item.getId())){
                            var taskCardPair=ViewFactory.createMinimizedCard(item.getId());
                            cache.put(item.getId(),taskCardPair.getValue());
                            taskCardControllers.add(taskCardPair.getKey());
                        }
                        setGraphic(cache.get(item.getId()));
                    }
                };
            }
        });

        taskCards.setOnMouseClicked(event -> {
            Long id=taskCards.getSelectionModel().getSelectedItem().getId();
            taskCards.getSelectionModel().clearSelection();
            mainCtrl.showCard(id);
        });
    }

    public void update(){
        TaskList updatedTaskList=serverUtils.getTaskList(taskListId);
        System.out.println(updatedTaskList);
        Platform.runLater(()->{
            taskList_name.setText(updatedTaskList.getName());
            taskCards.setItems(FXCollections.observableArrayList(updatedTaskList.getTaskCards()));
        });
    }

    public void closePolling(){
        timer.cancel();
        for(MinimizedCardController cardController: taskCardControllers)
            if(cardController!=null)
                cardController.closePolling();
    }
}
