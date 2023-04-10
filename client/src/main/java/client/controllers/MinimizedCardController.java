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
import javafx.scene.control.ListView;
import javafx.scene.effect.Bloom;
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

    private BoardController boardController;


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

    /**
     *
     */
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

    /**
     *
     * @param card
     */
    private void descIndicator(TaskCard card) {

        if (card.getDescription() != null && !card.getDescription().isBlank()) {
            desc.setStyle("-fx-font-family: FontAwesome; -fx-fill: " + card.getFontID());
            desc.setOpacity(1L);
        }else {
            desc.setOpacity(0L);
        }
    }

    /**
     *
     * @param card
     */
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

    /**
     *
     * @param card
     */
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

                tagsIndicatorHelper(tagArray, i);
            }

            tagBox.setOpacity(1L);
        }else{
            tagBox.setOpacity(0L);
        }
    }

    /**
     *
     * @param tagArray
     * @param index
     */
    private void tagsIndicatorHelper(Object[] tagArray, int index){

        if( index == 1 && ((Tag) tagArray[index]).getColor() != null)
            tag2.setStyle("-fx-background-color: " + ((Tag) tagArray[index]).getColor());

        if( index == 2 && ((Tag) tagArray[index]).getColor() != null)
            tag3.setStyle("-fx-background-color: " + ((Tag) tagArray[index]).getColor());
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

    /**
     *
     */
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