/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.controllers;

import client.views.ViewFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import models.Board;
import models.TaskList;

public class MainCtrl {
    private Stage primaryStage;
    private Stage addBoardStage;
    private Stage addTaskListStage;
    private Stage editBoardStage;
    private Stage cardStage;
    private ViewFactory viewFactory;
    private Scene primaryScene;

    public void initialize(Stage primaryStage, ViewFactory viewFactory) {
        this.primaryStage = primaryStage;
        this.viewFactory = viewFactory;
        showStartingPage();
    }

    public void showLoginPage() {
        var loginPage = viewFactory.createLogin();
        primaryScene.setRoot(loginPage.getValue());
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }


    public void showStartingPage() {
        var startingPage = viewFactory.createStartingPage();
        if(primaryScene==null)
            primaryScene=new Scene(startingPage.getValue());
        else
            primaryScene.setRoot(startingPage.getValue());
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Starting Page");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void showTaskList(TaskList selectedItem, BoardController boardController) {
        var taskList = viewFactory.createTaskList(selectedItem.getId(),boardController);
        primaryStage.setScene(new Scene(taskList.getValue()));
        primaryStage.setTitle("TaskList");
        primaryStage.show();
    }

    public void showAddBoardPage() {
        var addBoard = viewFactory.createAddBoard();
        addBoardStage = new Stage(StageStyle.UNDECORATED);
        addBoardStage.setScene(new Scene(addBoard.getValue()));
        addBoardStage.setTitle("Add Board");
        addBoardStage.initModality(Modality.APPLICATION_MODAL);
        addBoardStage.showAndWait();
    }

    public void showEditBoardPage(Board board) {
        var editBoard = viewFactory.createEditBoard(board);
        editBoardStage = new Stage(StageStyle.UNDECORATED);
        editBoardStage.setScene(new Scene(editBoard.getValue()));
        editBoardStage.setTitle("Edit Board");
        editBoardStage.initModality(Modality.APPLICATION_MODAL);
        editBoardStage.showAndWait();
    }

    public void showBoard(Board selectedItem) {
        var board = viewFactory.createBoard(selectedItem);
        primaryStage.setScene(new Scene(board.getValue()));
        primaryStage.setTitle("Board");
        primaryStage.show();
    }

    public void showCard(Long cardID) {
        var card = viewFactory.createCard(cardID);
        cardStage = new Stage(StageStyle.UNDECORATED);
        cardStage.setScene(new Scene(card.getValue()));
        cardStage.setTitle("Card Details");
        cardStage.initModality(Modality.APPLICATION_MODAL);
        cardStage.showAndWait() ;
    }

    public void closeCard() {
        if (cardStage!=null)
            cardStage.close();
    }

    public void showClientOverview(Board board) {
        var clientOverview = viewFactory.createClientOverview(board);
        primaryScene.setRoot(clientOverview.getValue());
        primaryStage.setTitle("Client Overview");
        primaryStage.setOnCloseRequest(event -> {
            if (clientOverview.getKey() != null)
                clientOverview.getKey().closePolling();
        });
        primaryStage.show();
    }

    public void closeAddBoard() {
        if (addBoardStage != null)
            addBoardStage.close();
    }

    public void closeEditBoard() {
        if (editBoardStage != null)
            editBoardStage.close();
    }

    public void showAddTaskListPage(Long boardId) {
        var addTaskList = viewFactory.createAddTaskList(boardId);
        addTaskListStage = new Stage(StageStyle.UNDECORATED);
        addTaskListStage.setScene(new Scene(addTaskList.getValue()));
        addTaskListStage.setTitle("Add Task List");
        addTaskListStage.initModality(Modality.APPLICATION_MODAL);
        addTaskListStage.showAndWait();
    }

    public void closeAddTaskListPage() {
        if (addTaskListStage != null)
            addTaskListStage.close();
    }

    public Pair<ClientMenuController, Parent> createClientMenu(Board board) {
        return viewFactory.createClientMenu(board);
    }

    public Pair<BoardController, Parent> createBoard(Board board) {
        return viewFactory.createBoard(board);
    }

    public Pair<TaskListController, Parent> createTaskList(Long taskListId, BoardController boardController) {
        return viewFactory.createTaskList(taskListId,boardController);
    }

    public Pair<MinimizedCardController, Parent> createMinimizedCard(Long card_id) {
        return viewFactory.createMinimizedCard(card_id);
    }
}
