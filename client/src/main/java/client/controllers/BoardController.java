package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import models.Board;
import models.TaskList;

import java.net.URL;
import java.util.*;

public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long boardId;
    private Map<Long, Parent> cache;
    private Timer timer;
    private final List<TaskListController> taskListControllers=new ArrayList<>();
    private final StringProperty nameProperty=new SimpleStringProperty();
    @FXML
    private FlowPane board_parent;
    @FXML
    private Button addList_button;

    @Inject
    public BoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        board_parent.setHgap(10);
        board_parent.setVgap(10);

        cache=new HashMap<>();
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        },0,500);
    }

    private void update(){
        Board board=serverUtils.getBoard(boardId);
        System.out.println(board);

        nameProperty.set(board.getName());

        List<Parent> list=new ArrayList<>();
        for(TaskList taskList: board.getTaskLists()){
            if(!cache.containsKey(taskList.getId())){
                var taskListPair=ViewFactory.createTaskList(taskList.getId());
                taskListControllers.add(taskListPair.getKey());
                cache.put(taskList.getId(),taskListPair.getValue());
            }
            list.add(cache.get(taskList.getId()));
        }
        Platform.runLater(()->board_parent.getChildren().setAll(list));
    }

    public void closePolling(){
        timer.cancel();
        for(TaskListController taskListController: taskListControllers)
            if(taskListController!=null)
                taskListController.closePolling();
    }

    public StringProperty namePropertyProperty() {
        return nameProperty;
    }
}
