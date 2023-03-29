package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import models.Board;

import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Board board;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchor_pane;
    private final Map<Long, Parent> taskListCache =new ConcurrentHashMap<>();
    private final Map<Long, Parent> taskCardCache =new ConcurrentHashMap<>();
    private final List<TaskListController> taskListControllers = new ArrayList<>();
    private final StringProperty nameProperty = new SimpleStringProperty();
    @FXML
    private FlowPane board_parent;
    @FXML
    private Button addList_button;
    @FXML
    private AnchorPane overlay;

    @Inject
    public BoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseScene();
        startLongPolling();

        addList_button.setOnMouseClicked(event -> {
            overlay.setVisible(true);
            mainCtrl.showAddTaskListPage(board.getId());
            overlay.setVisible(false);
        });
    }

    private void initialiseScene(){
        overlay.setVisible(false);
        board_parent.setHgap(30);
        board_parent.setVgap(30);

        anchor_pane.prefWidthProperty().bind(scrollPane.widthProperty());
        anchor_pane.prefHeightProperty().bind(scrollPane.heightProperty());

        nameProperty.set(board.getName());
        anchor_pane.setBackground(new Background(new BackgroundFill(Color.web(board.getBackgroundColor()),
                CornerRadii.EMPTY, Insets.EMPTY)));
        try{
            board=serverUtils.getBoard(board.getId());
            nameProperty.set(board.getName());
            anchor_pane.setBackground(new Background(new BackgroundFill(Color.web(board.getBackgroundColor()),
                    CornerRadii.EMPTY, Insets.EMPTY)));
            List<Long> ids=serverUtils.getTaskListsId(board.getId());
            List<Parent> taskLists=convertScenesFromTaskListIds(ids);
            board_parent.getChildren().setAll(taskLists);
        }catch (WebApplicationException e){
            closePolling();
            mainCtrl.showLoginPage();
        }
    }

    private void startLongPolling(){
        registerDetailsUpdates(updatedBoard -> {
            board=updatedBoard;
            Platform.runLater(() -> {
                nameProperty.set(board.getName());
                anchor_pane.setBackground(new Background(new BackgroundFill(Color.valueOf(board.getBackgroundColor()),
                        CornerRadii.EMPTY, Insets.EMPTY)));
            });
        });
        registerTaskListIdsUpdates(ids -> {
            List<Parent> list = convertScenesFromTaskListIds(ids);
            Platform.runLater(()-> board_parent.getChildren().setAll(list));
        });
    }

    private final ExecutorService detailUpdatesExecutor= Executors.newSingleThreadExecutor();
    private final ExecutorService taskListIdsUpdatesExecutor=Executors.newSingleThreadExecutor();

    private void registerDetailsUpdates(Consumer<Board> consumer){
        detailUpdatesExecutor.submit(()->{
            while(!detailUpdatesExecutor.isShutdown()){
                System.out.println("register for details updates...");
                var response=serverUtils.getBoardUpdates(board.getId());
                System.out.println(response.getStatus());
                if(response.getStatus()==204)
                    continue;
                if(response.getStatus()==400){
                    closePolling();
                    Platform.runLater(mainCtrl::showLoginPage);
                    return;
                }
                var board=response.readEntity(Board.class);
                consumer.accept(board);
            }
        });
    }

    private void registerTaskListIdsUpdates(Consumer<List<Long>> consumer){
        taskListIdsUpdatesExecutor.submit(()->{
            while (!taskListIdsUpdatesExecutor.isShutdown()){
                System.out.println("register for taskList ids updates...");
                var response=serverUtils.getTaskListIdsUpdates(board.getId());
                System.out.println(response.getStatus());
                if(response.getStatus()==204)
                    continue;
                List<Long> ids=response.readEntity(new GenericType<>() {});
                consumer.accept(ids);
            }
        });
    }

    public void closePolling() {
        detailUpdatesExecutor.shutdown();
        taskListIdsUpdatesExecutor.shutdown();
        for (TaskListController taskListController : taskListControllers)
            if (taskListController != null)
                taskListController.closePolling();
    }

    public StringProperty namePropertyProperty() {
        return nameProperty;
    }

    private List<Parent> convertScenesFromTaskListIds(List<Long> ids){
        List<Parent> list=new ArrayList<>();
        for(var id: ids){
            if (!taskListCache.containsKey(id)) {
                var taskListPair = mainCtrl.createTaskList(id,this);
                taskListControllers.add(taskListPair.getKey());
                taskListCache.put(id, taskListPair.getValue());
            }
            list.add(taskListCache.get(id));
        }
        return list;
    }

    public Map<Long, Parent> getTaskCardCache() {
        return taskCardCache;
    }
}
