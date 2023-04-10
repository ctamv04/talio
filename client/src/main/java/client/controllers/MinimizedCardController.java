package client.controllers;

import client.utils.ServerUtils;
import client.utils.WebsocketUtils;
import com.google.inject.Inject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import models.Tag;
import models.TaskCard;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class MinimizedCardController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskCardId;
    private final WebsocketUtils websocketUtils;

    private final BoardController boardController;
    private TaskListController taskListController;


    @FXML
    private Button close_button;
    @FXML
    private Text card_name;
    @FXML
    private StackPane minBG;
    @FXML
    private Label progress;
    @FXML
    private FontAwesomeIconView desc;
    @FXML
    private HBox tag1;
    @FXML
    private HBox tag2;
    @FXML
    private HBox tag3;
    @FXML
    private HBox tagBox;

    @Inject
    public MinimizedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long taskCardId,
                                   WebsocketUtils websocketUtils, BoardController boardController,
                                   TaskListController taskListController) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskCardId = taskCardId;
        this.websocketUtils = websocketUtils;
        this.boardController = boardController;
        this.taskListController = taskListController;
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
            card_name.setStyle("-fx-fill:" +taskCard.getFontID() + ";");

            descIndicator(taskCard);
            subsIndicator(taskCard);
            tagsIndicator(taskCard);
        }catch (WebApplicationException e){
            stopWebsockets();
        }
    }

    private void descIndicator(TaskCard card) {

        if (card.getDescription() != null && !card.getDescription().isBlank()) {
            desc.setStyle("-fx-font-family: FontAwesome; -fx-fill: " + card.getFontID());
            desc.setOpacity(1L);
        }else {
            desc.setOpacity(0L);
        }
    }

    private void subsIndicator(TaskCard card) {

        if (card.getSubs() != null && card.getSubs().size() > 0) {

            var subs = card.getSubs();
            AtomicInteger completed = new AtomicInteger();
            completed.set(0);

            subs.forEach((a, b) -> {
                if (b)
                    completed.getAndIncrement();
            });

            progress.setText(completed.get() + "/" + subs.size());
            progress.setStyle("-fx-text-fill: " + card.getFontID());
            progress.setOpacity(1L);
        } else {
            progress.setOpacity(0L);
        }
    }

    private void tagsIndicator(TaskCard card) {
        if(card.getTags() != null && card.getTags().size() != 0){
            var tags = card.getTags();
            tag1.setStyle("-fx-background-color: transparent");
            tag2.setStyle("-fx-background-color: transparent");
            tag3.setStyle("-fx-background-color: transparent");

            var tagArray = tags.toArray();
            for(int i = 0; i < tagArray.length; i++){

                if( i == 0 && ((Tag) tagArray[i]).getColor() != null)
                    tag1.setStyle("-fx-background-color: " + ((Tag) tagArray[i]).getColor());

                if( i == 1 && ((Tag) tagArray[i]).getColor() != null)
                    tag2.setStyle("-fx-background-color: " + ((Tag) tagArray[i]).getColor());

                if( i == 2 && ((Tag) tagArray[i]).getColor() != null)
                    tag3.setStyle("-fx-background-color: " + ((Tag) tagArray[i]).getColor());
            }

            tagBox.setOpacity(1L);
        }else{
            tagBox.setOpacity(0L);
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
            card_name.setStyle("-fx-fill:" +updatedTaskCard.getFontID() + ";");

            descIndicator(updatedTaskCard);
            subsIndicator(updatedTaskCard);
            tagsIndicator(updatedTaskCard);
        }));
    }

    public void stopWebsockets(){
        websocketUtils.unsubscribeFromMessages("/topic/taskcard/"+taskCardId);
    }

    public void delete() {
        serverUtils.deleteMinimizedCard(this.taskCardId);
    }

    public void Highlight() {
        taskListController.taskCards.requestFocus();
        taskListController.taskCards.getSelectionModel().select(this.taskCardId);
    }

    public void StopHighlight() {
        taskListController.taskCards.getSelectionModel().clearSelection();
    }

    public void setTaskListController(TaskListController taskListController) {
        this.taskListController = taskListController;
    }
}