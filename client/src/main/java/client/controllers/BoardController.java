package client.controllers;

import client.utils.BoardUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Stage;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Data;
import models.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class BoardController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private Board board;
    private BoardUtils boardUtils;
    private boolean shortcutsOpen;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchor_pane;
    private final Map<Long, Parent> taskListCache =new ConcurrentHashMap<>();
    private final Map<Long, Parent> taskCardCache =new ConcurrentHashMap<>();
    private final List<TaskListController> taskListControllers = new ArrayList<>();
    private final IntegerProperty changeDetector = new SimpleIntegerProperty(0);
    private boolean isActive=true;
    @FXML
    private FlowPane board_parent;
    @FXML
    private Button addList_button;
    @FXML
    private AnchorPane overlay;

    @Inject
    public BoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board,
                           BoardUtils boardUtils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
        this.boardUtils = boardUtils;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseScene();
        startLongPolling();


        shortcutsOpen = false;
        mainCtrl.getPrimaryScene().getAccelerators().put(KeyCombination.valueOf("K"), this::openShortcuts);

        addList_button.setOnMouseClicked(this::onAddListButton);
    }

    private void openShortcuts() {
        if (shortcutsOpen) {
            shortcutsOpen = false;
            mainCtrl.closeShortcuts();
        } else {
            shortcutsOpen = true;
            mainCtrl.showShortcutsPage();
        }
    }

    public void onAddListButton(Event event){
        overlay.setVisible(true);
        mainCtrl.showAddTaskListPage(board.getId());
        overlay.setVisible(false);
    }

    public void initialiseScene(){
        overlay.setVisible(false);
        board_parent.setHgap(30);
        board_parent.setVgap(30);
        anchor_pane.prefWidthProperty().bind(scrollPane.widthProperty());
        anchor_pane.prefHeightProperty().bind(scrollPane.heightProperty());

        try{
            board=serverUtils.getBoard(board.getId());
            changeDetector.set(1-changeDetector.get());
            anchor_pane.setBackground(new Background(new BackgroundFill(Color.web(board.getBackgroundColor()),
                    CornerRadii.EMPTY, Insets.EMPTY)));
            List<Long> ids=serverUtils.getTaskListsId(board.getId());
            List<Parent> taskLists=boardUtils.convertScenesFromTaskListIds(ids,taskListCache,this,taskListControllers);
            board_parent.getChildren().setAll(taskLists);
            board_parent.getChildren().add(addList_button);
        }catch (WebApplicationException e){
            closePolling();
            mainCtrl.showLoginPage();
        }
    }

    private final ExecutorService detailUpdatesExecutor= Executors.newSingleThreadExecutor();
    private final ExecutorService taskListIdsUpdatesExecutor=Executors.newSingleThreadExecutor();

    public void startLongPolling(){
        boardUtils.registerDetailsUpdates(updatedBoard -> {
            board=updatedBoard;
            Platform.runLater(() -> {
                changeDetector.set(1-changeDetector.get());
                anchor_pane.setBackground(new Background(new BackgroundFill(Color.valueOf(board.getBackgroundColor()),
                        CornerRadii.EMPTY, Insets.EMPTY)));
            });
        },board,detailUpdatesExecutor,this);
        boardUtils.registerTaskListIdsUpdates(ids -> {
            List<Parent> list = boardUtils.convertScenesFromTaskListIds(ids,taskListCache,this,taskListControllers);
            Platform.runLater(()-> {
                board_parent.getChildren().setAll(list);
                board_parent.getChildren().add(addList_button);
            });
        },board,taskListIdsUpdatesExecutor);
    }

    public void closePolling() {
        boardUtils.closePolling(detailUpdatesExecutor);
        boardUtils.closePolling(taskListIdsUpdatesExecutor);
        for (TaskListController taskListController : taskListControllers)
            if (taskListController != null)
                taskListController.closePolling();
    }
}
