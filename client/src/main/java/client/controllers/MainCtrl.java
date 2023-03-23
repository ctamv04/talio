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
import javafx.util.Pair;
import models.Board;
import models.TaskList;

public class MainCtrl {
    private Stage primaryStage;
    private Stage addBoardStage;
    private Stage addTaskListStage;
    private Stage cardStage;
    private ViewFactory viewFactory;

    public void initialize(Stage primaryStage, ViewFactory viewFactory) {
        this.primaryStage = primaryStage;
        this.viewFactory = viewFactory;
        showStartingPage();
    }

    public void showLoginPage(String port) {
        var loginPage = viewFactory.createLogin(port);
        primaryStage.setScene(new Scene(loginPage.getValue()));
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }


    public void showStartingPage() {
        var startingPage = viewFactory.createStartingPage();
        primaryStage.setScene(new Scene(startingPage.getValue()));
        primaryStage.setTitle("Starting Page");
        primaryStage.show();
    }

    public void showTaskList(String port, TaskList selectedItem) {
        var taskList = viewFactory.createTaskList(port, selectedItem.getId());
        primaryStage.setScene(new Scene(taskList.getValue()));
        primaryStage.setTitle("TaskList");
        primaryStage.show();
    }

    public void showAddBoardPage(String port) {
        var addBoard = viewFactory.createAddBoard(port);
        addBoardStage = new Stage();
        addBoardStage.setScene(new Scene(addBoard.getValue()));
        addBoardStage.setTitle("Add Board");
        addBoardStage.initModality(Modality.APPLICATION_MODAL);
        addBoardStage.showAndWait();
    }

    public void showBoard(String port, Board selectedItem) {
        var board = viewFactory.createBoard(port, selectedItem.getId());
        primaryStage.setScene(new Scene(board.getValue()));
        primaryStage.setTitle("Board");
        primaryStage.show();
    }

    public void showCard(String port, Long card_id) {
        var card = viewFactory.createCard(port, card_id);
        cardStage = new Stage();
        cardStage.setScene(new Scene(card.getValue()));
        cardStage.setTitle("Card Details");
        cardStage.initModality(Modality.APPLICATION_MODAL);
        cardStage.showAndWait();
    }

    public void closeCard() {
        if (cardStage != null)
            cardStage.close();
    }

    public void showClientOverview(String port, Long boardId) {
        var clientOverview = viewFactory.createClientOverview(port, boardId);

        primaryStage.setScene(new Scene(clientOverview.getValue()));
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

    public void showAddTaskListPage(String port, Long boardId) {
        var addTaskList = viewFactory.createAddTaskList(port, boardId);
        addTaskListStage = new Stage();
        addTaskListStage.setScene(new Scene(addTaskList.getValue()));
        addTaskListStage.setTitle("Add Task List");
        addTaskListStage.initModality(Modality.APPLICATION_MODAL);
        addTaskListStage.showAndWait();
    }

    public void closeAddTaskListPage() {
        if (addTaskListStage != null)
            addTaskListStage.close();
    }

    public Pair<ClientMenuController, Parent> createClientMenu(String port) {
        return viewFactory.createClientMenu(port);
    }

    public Pair<BoardController, Parent> createBoard(String port, Long boardId) {
        return viewFactory.createBoard(port, boardId);
    }

    public Pair<TaskListController, Parent> createTaskList(String port, Long id) {
        return viewFactory.createTaskList(port, id);
    }

    public Pair<MinimizedCardController, Parent> createMinimizedCard(String port, Long card_id) {
        return viewFactory.createMinimizedCard(port, card_id);
    }
}
