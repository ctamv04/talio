package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
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

import java.net.URL;
import java.util.*;
import java.util.List;

public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Board board;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public AnchorPane anchor_pane;
    private Map<Long, Parent> cache;
    private Timer timer;
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
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 500);

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

    private void update() {
        try {
            board = serverUtils.getBoard(board.getId());
            List<Long> taskListsId = serverUtils.getTaskListsId(board.getId());

            nameProperty.set(board.getName());

            List<Parent> list = new ArrayList<>();

            for (var id : taskListsId) {
                if (!cache.containsKey(id)) {
                    var taskListPair = mainCtrl.createTaskList(id);

                    taskListControllers.add(taskListPair.getKey());
                    cache.put(id, taskListPair.getValue());
                }
                list.add(cache.get(id));
            }
            Platform.runLater(() -> board_parent.getChildren().setAll(list));
        } catch (WebApplicationException e) {
            closePolling();

            Platform.runLater(mainCtrl::showLoginPage);
        }
    }

    public void closePolling() {
        timer.cancel();
        for (TaskListController taskListController : taskListControllers)
            if (taskListController != null)
                taskListController.closePolling();
    }

    public StringProperty namePropertyProperty() {
        return nameProperty;
    }
}
