package client.controllers;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import models.TaskCard;
import models.TaskList;

import java.net.URL;
import java.util.*;

import static java.lang.Math.min;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskListId;
    private Map<Long, Parent> cache;
    private Timer timer;
    @FXML
    private Label taskList_name;
    @FXML

    public ListView<Long> taskCards;
    private final List<MinimizedCardController> taskCardControllers = new ArrayList<>();


    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, long taskListId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskListId = taskListId;
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cache = new HashMap<>();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }

        }, 0, 500);

        for (Node node : taskCards.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                final ScrollBar bar = (ScrollBar) node;
                bar.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
                        System.out.println(bar.getOrientation() + " " + newValue);
                    }
                });
            }
        }

        taskCards.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Long> call(ListView<Long> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }

                        if (!cache.containsKey(item)) {
                            Random random = new Random();
                            var taskCardPair = mainCtrl.createMinimizedCard(serverUtils.getServer(), item);
                            taskCardPair.getValue().setStyle("-fx-background-color: rgb(" +
                                    random.nextInt(256) + "," + random.nextInt(256) + "," + random.nextInt(256) + ")");
                            cache.put(item, taskCardPair.getValue());
                            taskCardControllers.add(taskCardPair.getKey());
                        }
                        setGraphic(cache.get(item));
                    }
                };
            }
        });

        taskCards.setOnMouseClicked(event -> {
            Long cardId = taskCards.getSelectionModel().getSelectedItem();
            if (cardId != null) {
                taskCards.getSelectionModel().clearSelection();
                mainCtrl.showCard(serverUtils.getServer(), cardId);
            }
        });

        taskCards.setOnDragDetected(event -> {
            Dragboard dragboard = taskCards.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(taskCards.getSelectionModel().getSelectedItem().toString() + " " + taskListId);
            dragboard.setContent(content);
            event.consume();
        });

        taskCards.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                int index = findIndex(event);
                System.out.println(index);
            }
            event.consume();
        });

        taskCards.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                String item = dragboard.getString();
                Long id = Long.parseLong(item.split(" ")[0]);
                Long list1 = Long.parseLong(item.split(" ")[1]);
                int index = findIndex(event);
                if (taskListId.equals(list1)) {
                    int initialPos = serverUtils.getTaskCard(id).getPosition();
                    if (index > initialPos)
                        index--;
                }
                serverUtils.swapBetweenLists(id, index, list1, taskListId);
                System.out.println(id + " " + list1 + " " + taskListId + " " + index + " ");

                success = true;
                taskCards.getSelectionModel().clearSelection();
                taskList_name.getParent().requestFocus();
            }
            event.setDropCompleted(success);
            event.consume();
        });

        taskCards.setFixedCellSize(60);
    }

    private int findIndex(DragEvent event) {
        double dropY = event.getSceneY();
        double listViewY = taskCards.localToScene(taskCards.getBoundsInLocal()).getMinY();
        double cellHeight = taskCards.getFixedCellSize();
        double poz = (dropY - listViewY) / cellHeight;
        int index = (int) poz;
        if (((int) (poz * 10)) % 10 >= 5)
            index++;
        return min(index, taskCards.getItems().size());
    }

    /**
     * Update the task cards in the list using pooling.
     */
    public void update() {
        try {
            TaskList updatedTaskList = serverUtils.getTaskList(taskListId);
            List<Long> taskCardsId = serverUtils.getTaskCardsId(taskListId);

//            System.out.println(updatedTaskList);
//            System.out.println(taskCardsId);

            Platform.runLater(() -> {
                taskList_name.setText(updatedTaskList.getName());
                taskCards.setItems(FXCollections.
                        observableArrayList(taskCardsId));
            });
        } catch (WebApplicationException e) {
            closePolling();
        }
    }

    /**
     * Stops the pooling after closing the scene.
     */
    public void closePolling() {
        timer.cancel();
        for (MinimizedCardController cardController : taskCardControllers)
            if (cardController != null)
                cardController.closePolling();
    }

    /**
     * Adds a new task card.
     */
    public void addTaskCard() {
        TaskCard card = new TaskCard();
        mainCtrl.showCard(serverUtils.getServer(), serverUtils.addTaskCard(card, taskListId).getId());
    }

    /**
     * Removes a task list.
     */
    public void removeTaskList() {
        closePolling();
        serverUtils.removeTaskList(taskListId);
    }
}
