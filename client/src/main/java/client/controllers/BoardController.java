package client.controllers;

import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import models.Board;

import javax.swing.text.html.parser.Entity;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Board board;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public AnchorPane anchor_pane;
    private Map<Long, Parent> cache;
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
        overlay.setVisible(false);

        board_parent.setHgap(30);
        board_parent.setVgap(30);

        nameProperty.set(board.getName());

        anchor_pane.prefWidthProperty().bind(scrollPane.widthProperty());

        cache = new HashMap<>();
        registerDetailsUpdates(updatedBoard -> {
            board=updatedBoard;
            Platform.runLater(() -> nameProperty.set(board.getName()));
        });
        registerTaskListIdsUpdates(ids -> {
            if(ids==null)
                return;
            List<Parent> list = new ArrayList<>();
            for(var id: ids){
                if (!cache.containsKey(id)) {
                    var taskListPair = mainCtrl.createTaskList(id);
                    taskListControllers.add(taskListPair.getKey());
                    cache.put(id, taskListPair.getValue());
                }
                list.add(cache.get(id));
            }
            Platform.runLater(()-> board_parent.getChildren().setAll(list));
        });

        addList_button.setOnMouseClicked(event -> {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            overlay.setPrefWidth(bounds.getWidth());
            overlay.setPrefHeight(bounds.getHeight());
            overlay.setVisible(true);
            mainCtrl.showAddTaskListPage(board.getId());
            overlay.setVisible(false);
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

    @SuppressWarnings("all")
    private void registerTaskListIdsUpdates(Consumer<List<Long>> consumer){
        taskListIdsUpdatesExecutor.submit(()->{
            while (!taskListIdsUpdatesExecutor.isShutdown()){
                System.out.println("register for taskList ids updates...");
                var response=serverUtils.getTaskListIdsUpdates(board.getId());
                System.out.println(response.getStatus());
                if(response.getStatus()==204)
                    continue;
                List<Long> ids=response.readEntity(new GenericType<List<Long>>(){});
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
}
