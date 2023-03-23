package client.controllers;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import models.TaskCard;
import models.TaskList;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static java.lang.Math.min;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskListId;
    @FXML
    public Pane indicator_pane;
    @FXML
    private ScrollPane scrollPane;
    private Map<Long, Parent> cache;
    private Timer timer;
    @FXML
    private Label taskList_name;
    @FXML
    public ListView<Long> taskCards;
    private final List<MinimizedCardController> taskCardControllers=new ArrayList<>();
    private double scrolledY=0;
    Label rectangle=new Label("Sorin");


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

        indicator_pane.prefHeightProperty().bind(taskCards.heightProperty());
        taskCards.setFixedCellSize(60);
        taskCards.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            scrolledY=newValue.doubleValue()*(taskCards.getItems().size()*taskCards.getFixedCellSize()-300);
            System.out.println(scrolledY);
        });

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
                            var taskCardPair = mainCtrl.createMinimizedCard(item);
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
                mainCtrl.showCard(cardId);
            }
        });

        taskCards.setOnDragDetected(this::onDragDetected);
        taskCards.setOnDragOver(this::onDragOver);
        taskCards.setOnDragDropped(this::onDragDropped);

        scrollPane.setOnDragOver(this::onDragOver);
        scrollPane.setOnDragDropped(this::onDragDropped);

        indicator_pane.getChildren().add(rectangle);
    }

    private void onDragDetected(MouseEvent event){
        Dragboard dragboard = taskCards.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(taskCards.getSelectionModel().getSelectedItem().toString()+" "+taskListId);
        dragboard.setContent(content);
        event.consume();
    }

    private void onDragOver(DragEvent event){
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
            int index=findIndex(event);
            System.out.println(index);
            rectangle.setLayoutX(event.getX());
            rectangle.setLayoutY(findY(index));
            System.out.println(event.getSceneX());
        }
        event.consume();
    }

    private void onDragDropped(DragEvent event){
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasString()) {
            String item = dragboard.getString();
            Long id=Long.parseLong(item.split(" ")[0]);
            Long list1=Long.parseLong(item.split(" ")[1]);
            int index=findIndex(event);
            if(taskListId.equals(list1)){
                int initialPos=serverUtils.getTaskCard(id).getPosition();
                if(index>initialPos)
                    index--;
            }
            serverUtils.swapBetweenLists(id, index,list1,taskListId);
            System.out.println(id+" "+list1+" "+taskListId+" "+index+" ");
            success = true;
            taskCards.getSelectionModel().clearSelection();
            taskList_name.getParent().requestFocus();
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private int findIndex(DragEvent event) {
        double dropY = event.getSceneY();
        double listViewY = taskCards.localToScene(scrollPane.getBoundsInLocal()).getMinY();
        double cellHeight = taskCards.getFixedCellSize();
        System.out.println(dropY+" "+scrolledY+" "+listViewY+" "+cellHeight);
        double poz = (dropY + scrolledY - listViewY) / cellHeight;
        int index = (int) poz;
        if (((int) (poz * 10)) % 10 >= 5)
            index++;
        return min(index, taskCards.getItems().size());
    }

    private double findY(int index){
        return index*taskCards.getFixedCellSize()-scrolledY;
    }

    /**
     * Update the task cards in the list using pooling.
     */
    public void update() {
        try {
            TaskList updatedTaskList = serverUtils.getTaskList(taskListId);
            List<Long> taskCardsId = serverUtils.getTaskCardsId(taskListId);

            Platform.runLater(()->{
                taskList_name.setText(updatedTaskList.getName());
                taskCards.setItems(FXCollections.
                                    observableArrayList(taskCardsId));
                taskCards.setMaxHeight(taskCards.getFixedCellSize()*taskCardsId.size());
                taskCards.setMinHeight(taskCards.getFixedCellSize()*taskCardsId.size());
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
        mainCtrl.showCard(serverUtils.addTaskCard(card, taskListId).getId());
    }

    /**
     * Removes a task list.
     */
    public void removeTaskList() {
        closePolling();
        serverUtils.removeTaskList(taskListId);
    }
}
