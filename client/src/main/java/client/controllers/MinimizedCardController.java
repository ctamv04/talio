package client.controllers;

import client.utils.ServerUtils;
import client.utils.WebsocketUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import models.TaskCard;

import java.net.URL;
import java.util.ResourceBundle;

public class MinimizedCardController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskCardId;
    private final WebsocketUtils websocketUtils;
    private BoardController boardController;

    @FXML
    private Button close_button;
    @FXML
    private Text card_name;
    @FXML
    private StackPane minBG;

    @Inject
    public MinimizedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long taskCardId,
                                   WebsocketUtils websocketUtils, BoardController boardController) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskCardId = taskCardId;
        this.websocketUtils = websocketUtils;
        this.boardController = boardController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeScene();
        startWebsockets();
    }

    private void initializeScene() {
        try {
            TaskCard taskCard = serverUtils.getTaskCard(taskCardId);
            card_name.setText(taskCard.getName());
            minBG.setStyle("-fx-background-color:" + taskCard.getBackID() +"; ");
            minBG.getChildrenUnmodifiable().get(0).setStyle("-fx-fill:" +taskCard.getFontID() + ";");
        }catch (WebApplicationException e){
            stopWebsockets();
        }
    }


    private void startWebsockets() {
        websocketUtils.registerForMessages("/topic/taskcard/"+taskCardId, TaskCard.class, updatedTaskCard-> Platform.runLater(() -> {
            if (updatedTaskCard.getPosition() == -1) {
                stopWebsockets();
                return;
            }
            card_name.setText(updatedTaskCard.getName());
            minBG.setStyle("-fx-background-color:" + updatedTaskCard.getBackID() + "; ");
            minBG.getChildrenUnmodifiable().get(0).setStyle("-fx-fill:" + updatedTaskCard.getFontID() + ";");
        }));
    }

    public void stopWebsockets(){
        websocketUtils.unsubscribeFromMessages("/topic/taskcard/"+taskCardId);
    }

    public void delete() {
        serverUtils.deleteMinimizedCard(this.taskCardId);
    }

    public void Highlight() {
        minBG.setEffect(new Bloom(0.1));
        TaskListController controller = getTaskListController();
        controller.taskCards.requestFocus();
        controller.taskCards.getSelectionModel().select(this.taskCardId);
    }

    public void StopHighlight() {
        minBG.setEffect(null);
        TaskListController controller = getTaskListController();
        controller.taskCards.getSelectionModel().clearSelection();
    }

    private TaskListController getTaskListController() {
        TaskCard card = serverUtils.getTaskCard(taskCardId);
        Long listId = card.getTaskListId();
        return boardController.getTaskListControllers().stream().filter(cntrl -> cntrl.getTaskListId().equals(listId)).findFirst().get();
    }

    public Long getTaskCardId() {
        return taskCardId;
    }


}