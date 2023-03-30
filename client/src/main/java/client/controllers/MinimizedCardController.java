package client.controllers;

import client.utils.ServerUtils;
import client.utils.WebsocketUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    @FXML
    private Button close_button;
    @FXML
    private Text card_name;
    @FXML
    private StackPane minBG;

    @Inject
    public MinimizedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long taskCardId,
                                   WebsocketUtils websocketUtils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskCardId = taskCardId;
        this.websocketUtils = websocketUtils;
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
}