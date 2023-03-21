package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import models.Board;

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
        try{
            Board board=serverUtils.getBoard(boardId);
            List<Long> taskListsId=serverUtils.getTaskListsId(boardId);

            System.out.println(board);
            System.out.println(taskListsId);

            nameProperty.set(board.getName());

            List<Parent> list = new ArrayList<>();
            for(var id: taskListsId){
                if(!cache.containsKey(id)){
                    var taskListPair=ViewFactory.createTaskList(id);
                    taskListControllers.add(taskListPair.getKey());
                    cache.put(id,taskListPair.getValue());
                }
                list.add(cache.get(id));
            }
            Platform.runLater(()->board_parent.getChildren().setAll(list));
        }catch (WebApplicationException e){
            closePolling();
            mainCtrl.showLoginPage();
        }
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
