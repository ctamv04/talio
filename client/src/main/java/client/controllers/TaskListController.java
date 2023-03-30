package client.controllers;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Callback;
import models.TaskCard;
import models.TaskList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.lang.Math.min;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskListId;
    private final BoardController boardController;
    @FXML
    public Pane indicator_pane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label taskList_name;
    @FXML
    private TextField editTaskList_Name;
    @FXML
    public ListView<Long> taskCards;
    private final List<MinimizedCardController> taskCardControllers = new ArrayList<>();
    private final Line line = new Line();
    private int entries = 0;

    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, Long taskListId, BoardController boardController) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskListId = taskListId;
        this.boardController = boardController;
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseScene();
        startPolling();

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

                        if (!boardController.getTaskCardCache().containsKey(item)) {

                            var taskCardPair = mainCtrl.createMinimizedCard(item);

                            boardController.getTaskCardCache().put(item, taskCardPair.getValue());
                            taskCardControllers.add(taskCardPair.getKey());
                        }
                        setGraphic(boardController.getTaskCardCache().get(item));
                    }
                };
            }
        });

        taskCards.setOnMouseClicked(event -> {

            if(event.getClickCount() == 2){
                Long cardId = taskCards.getSelectionModel().getSelectedItem();
                if (cardId != null) {
                    taskCards.getSelectionModel().clearSelection();
                    boardController.getOverlay().setVisible(true);
                    mainCtrl.showCard(cardId);
                    boardController.getOverlay().setVisible(false);
                }
            }

        });

        initialiseDragAndDrop();
    }

    private void initialiseScene() {
        indicator_pane.prefHeightProperty().bind(taskCards.heightProperty());
        taskCards.setFixedCellSize(60);
        taskCards.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        try {
            TaskList updatedTaskList = serverUtils.getTaskList(taskListId);
            List<Long> taskCardsId = serverUtils.getTaskCardsId(taskListId);

            taskList_name.setText(updatedTaskList.getName());
            taskCards.setItems(FXCollections.observableArrayList(taskCardsId));

            taskCards.setMaxHeight(taskCards.getFixedCellSize() * taskCardsId.size());
            taskCards.setMinHeight(taskCards.getFixedCellSize() * taskCardsId.size());

        } catch (WebApplicationException e) {
            closePolling();
        }
    }

    private void initialiseDragAndDrop() {
        taskCards.setOnDragDetected(this::onDragDetected);
        taskCards.setOnDragOver(this::onDragOver);
        taskCards.setOnDragDropped(this::onDragDropped);

        scrollPane.setOnDragOver(this::onDragOver);
        scrollPane.setOnDragDropped(this::onDragDropped);

        taskCards.setOnDragExited(event -> increment(1));
        taskCards.setOnDragEntered(event -> increment(-1));
        scrollPane.setOnDragExited(event -> increment(2));
        scrollPane.setOnDragEntered(event -> increment(-2));

        indicator_pane.getChildren().add(line);
        line.setStroke(Color.BLACK);
        line.getStrokeDashArray().addAll(10d, 10d);
        line.setVisible(false);
    }

    private void startPolling() {
        registerTaskCardIdsUpdates(ids -> Platform.runLater(() -> {
            taskCards.setItems(FXCollections.observableArrayList(ids));
            taskCards.setMaxHeight(taskCards.getFixedCellSize() * ids.size());
            taskCards.setMinHeight(taskCards.getFixedCellSize() * ids.size());
        }));
        registerDetailsUpdates(updatedTaskList -> Platform.runLater(() -> taskList_name.setText(updatedTaskList.getName())));
    }

    private void increment(int offset) {
        entries = entries + offset;
        line.setVisible(entries != 0);
    }

    /***
     * When a task card is dragged, this method is called.
     * @param event the mouse event
     */
    private void onDragDetected(MouseEvent event) {
        Dragboard dragboard = taskCards.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();

        content.putString(taskCards.getSelectionModel().getSelectedItem().toString() + " " + taskListId);

        WritableImage snapshot = boardController.getTaskCardCache().get(taskCards.getSelectionModel().getSelectedItem()).snapshot(new SnapshotParameters(), null);
        ImageView dragView = new ImageView(snapshot);
        dragView.setTranslateX(event.getX());
        dragView.setTranslateY(event.getY());

        dragboard.setContent(content);
        dragboard.setDragView(dragView.getImage());

        event.consume();
    }

    private void onDragOver(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
            int index = findIndex(event);
            line.setStartX(event.getX() - 100);
            line.setEndX(event.getX() + 100);
            double y = findY(index);
            line.setStartY(y);
            line.setEndY(y);
        }
        event.consume();
    }

    private void onDragDropped(DragEvent event) {
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
            serverUtils.swapBetweenLists(id, index,list1,taskListId);
            success = true;
            taskCards.getSelectionModel().clearSelection();
            taskList_name.getParent().requestFocus();
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private int findIndex(DragEvent event) {
        double dropY = event.getSceneY();
        double listViewY = scrollPane.localToScene(scrollPane.getBoundsInLocal()).getMinY();
        double cellHeight = taskCards.getFixedCellSize();
        double poz = (dropY + findScrolledY() - listViewY) / cellHeight;
        int index = (int) poz;
        if (((int) (poz * 10)) % 10 >= 5)
            index++;
        return min(index, taskCards.getItems().size());
    }

    private double findY(int index) {
        return index * taskCards.getFixedCellSize();
    }

    private double findScrolledY() {
        double ratio = scrollPane.getVvalue();
        return ratio * (taskCards.getItems().size() * taskCards.getFixedCellSize() - 300);
    }

    private final ExecutorService detailUpdatesExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService taskCardIdsUpdatesExecutor = Executors.newSingleThreadExecutor();

    /***
     * Looks whether there has been a valid request, and if so, calls the consumer with the updated
     * task list.
     * @param consumer the consumer to be called
     */
    private void registerDetailsUpdates(Consumer<TaskList> consumer) {
        detailUpdatesExecutor.submit(() -> {
            while (!detailUpdatesExecutor.isShutdown()) {
                var response = serverUtils.getTaskListUpdates(taskListId);
                if (response.getStatus() == 400) { // bad request
                    closePolling();
                    return;
                }
                if (response.getStatus() == 204) { // no updates
                    var taskList = response.readEntity(TaskList.class);
                    consumer.accept(taskList);
                }
            }
        });
    }

    private void registerTaskCardIdsUpdates(Consumer<List<Long>> consumer) {
        taskCardIdsUpdatesExecutor.submit(() -> {
            while (!taskCardIdsUpdatesExecutor.isShutdown()) {
                var response = serverUtils.getTaskCardIdsUpdates(taskListId);
                if (response.getStatus() == 204)
                    continue;
                List<Long> ids = response.readEntity(new GenericType<>() {
                });
                consumer.accept(ids);
            }
        });
    }

    /**
     * Stops the polling after closing the scene.
     */
    public void closePolling() {
        detailUpdatesExecutor.shutdown();
        taskCardIdsUpdatesExecutor.shutdown();
        for (MinimizedCardController cardController : taskCardControllers)
            if (cardController != null)
                cardController.stopWebsockets();
    }

    /**
     * Adds a new task card.
     */
    public void addTaskCard() {
        TaskCard card = new TaskCard();
        boardController.getOverlay().setVisible(true);
        mainCtrl.showCard(serverUtils.addTaskCard(card, taskListId).getId());
        boardController.getOverlay().setVisible(false);
    }

    /**
     * Removes a task list.
     */
    public void removeTaskList() {
        closePolling();
        serverUtils.removeTaskList(taskListId);
    }

    /**
     * Allows the user to edit the title of the Tasklist by hovering on its title area.
     */
    public void editTaskListNameHoverIn() {
        editTaskList_Name.setText(taskList_name.getText());
        editTaskList_Name.setOpacity(1);
        taskList_name.setOpacity(0);
    }


    /**
     * Saves the new title when the user hovers out of the TaskList's title area.
     */
    public void editTaskListNameHoverOut() {
        editTaskList_Name.setOpacity(0);
        taskList_name.setOpacity(1);
        taskList_name.setText(editTaskList_Name.getText());
        TaskList updatedTaskList = serverUtils.getTaskList(taskListId);
        updatedTaskList.setName(taskList_name.getText());
        serverUtils.updateTaskList(taskListId, updatedTaskList);

    }
}
