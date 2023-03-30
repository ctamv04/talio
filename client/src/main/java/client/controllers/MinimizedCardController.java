package client.controllers;

import client.utils.ServerUtils;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MinimizedCardController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskCardId;
    @FXML
    private Button close_button;
    @FXML
    private Text card_name;
    @FXML
    private StackPane minBG;

    @Inject
    public MinimizedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long taskCardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskCardId = taskCardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeScene();
        startLongPolling();
    }

    private void initializeScene() {
        try {
            TaskCard taskCard = serverUtils.getTaskCard(taskCardId);
            card_name.setText(taskCard.getName());
            minBG.setStyle("-fx-background-color:" + taskCard.getBackID() + "; ");
            minBG.getChildrenUnmodifiable().get(0).setStyle("-fx-fill:" + taskCard.getFontID() + ";");
            ;
        } catch (WebApplicationException e) {
            closePolling();
        }
    }

    private void startLongPolling() {
        registerDetailsUpdates(updatedTaskCard -> Platform.runLater(() -> card_name.setText(updatedTaskCard.getName())));
    }

    public void closePolling() {
        detailUpdatesExecutor.shutdown();
    }

    private final ExecutorService detailUpdatesExecutor = Executors.newSingleThreadExecutor();

    private void registerDetailsUpdates(Consumer<TaskCard> consumer) {
        detailUpdatesExecutor.submit(() -> {
            while (!detailUpdatesExecutor.isShutdown()) {
                System.out.println("register updates...");
                var response = serverUtils.getTaskCardUpdates(taskCardId);
                System.out.println(response.getStatus());
                if (response.getStatus() == 204)
                    continue;
                if (response.getStatus() == 400) {
                    closePolling();
                    return;
                }
                var taskCard = response.readEntity(TaskCard.class);
                minBG.setStyle("-fx-background-color:" + taskCard.getBackID() + "; ");
                minBG.getChildrenUnmodifiable().get(0).setStyle("-fx-fill:" + taskCard.getFontID() + ";");
                consumer.accept(taskCard);
            }
        });
    }

    public void delete() {
        serverUtils.deleteMinimizedCard(this.taskCardId);
    }
}
