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

import client.controllers.popups.EditBoardController;
import client.views.ViewFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import models.Board;
import models.Tag;
import models.TaskList;

import java.util.ArrayList;
import java.util.List;

public class MainCtrl {

    private ClientOverviewController clientOverview;
    private Stage primaryStage;
    private Stage addBoardStage;
    private Stage addTaskListStage;
    private Stage addTagStage;
    private Stage editBoardStage;
    private Stage cardStage;
    private Stage adminLoginStage;

    private Stage shortcutsStage;

    private Stage deletedBoardStage;
    private Stage deletedCardStage;
    private ViewFactory viewFactory;
    private Scene primaryScene;

    private boolean isAdmin;
    private List<Board> boards;

    public void initialize(Stage primaryStage, ViewFactory viewFactory) {
        this.primaryStage = primaryStage;
        this.viewFactory = viewFactory;

        var iconResource = getClass().getResource("/images/icon.png");
        if (iconResource != null)
            this.primaryStage.getIcons().add(new Image(iconResource.toString()));

        this.boards = new ArrayList<>();
        showLoginPage();
    }

    public void showMainPage() {
        var loginPage = viewFactory.createMainPage();
        primaryScene.setRoot(loginPage.getValue());
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }


    public void showLoginPage() {
        var startingPage = viewFactory.createLoginPage();

        if (primaryScene == null)
            primaryScene = new Scene(startingPage.getValue());
        else
            primaryScene.setRoot(startingPage.getValue());

        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Starting Page");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void showTaskList(TaskList selectedItem, BoardController boardController) {
        var taskList = viewFactory.createTaskList(selectedItem.getId(), boardController);
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

    public void showShortcutsPage() {
        var shortcuts = viewFactory.createShortcutsMenu();
        shortcutsStage = new Stage(StageStyle.UNDECORATED);
        var scene = new Scene(shortcuts.getValue());
        shortcutsStage.setScene(scene);
        shortcutsStage.setTitle("Shortcuts");
        shortcutsStage.showAndWait();
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showCard(Long cardID) {
        var card = viewFactory.createCard(cardID);
        cardStage = new Stage(StageStyle.UNDECORATED);
        var scene = new Scene(card.getValue());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                card.getKey().back();
            }
        });
        cardStage.setScene(scene);
        cardStage.setTitle("Card Details");
        cardStage.initModality(Modality.APPLICATION_MODAL);
        cardStage.showAndWait();
    }

    public void closeCard() {
        if (cardStage != null)
            cardStage.close();
    }

    public void closeShortcuts() {
        if (shortcutsStage != null) {
            shortcutsStage.close();
        }
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

    public ClientOverviewController getClientOverview() {
        return clientOverview;
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

    public void showUpdateTagPage(EditBoardController editBoardController, Tag tag) {
        var updateTag = viewFactory.createUpdateTag(editBoardController, tag);
        addTagStage = new Stage(StageStyle.UNDECORATED);
        addTagStage.setScene(new Scene(updateTag.getValue()));
        addTagStage.initModality(Modality.APPLICATION_MODAL);
        addTagStage.showAndWait();
    }

    public void closeAddTagPage() {
        if (addTagStage != null)
            addTagStage.close();
    }

    public Pair<ClientMenuController, Parent> createClientMenu(Board board, BoardController boardController) {
        return viewFactory.createClientMenu(board, boardController);
    }

    public Pair<BoardController, Parent> createBoard(Board board) {
        return viewFactory.createBoard(board);
    }

    public Pair<TaskListController, Parent> createTaskList(Long taskListId, BoardController boardController) {
        return viewFactory.createTaskList(taskListId, boardController);
    }

    public Pair<MinimizedCardController, Parent> createMinimizedCard(Long card_id, BoardController boardController) {
        return viewFactory.createMinimizedCard(card_id, boardController);
    }

    public void showAdminLogin() {
        var adminLogin = viewFactory.createAdminLogin();
        adminLoginStage = new Stage(StageStyle.UNDECORATED);
        adminLoginStage.setScene(new Scene(adminLogin.getValue()));
        adminLoginStage.setTitle("Login as admin");
        adminLoginStage.initModality(Modality.APPLICATION_MODAL);
        adminLoginStage.showAndWait();
    }

    public void closeAdminLogin() {
        if (adminLoginStage != null)
            adminLoginStage.close();
        showMainPage();
    }

    public void showDeletedBoard() {
        var addDeleteBoard = viewFactory.createBoardDeleted();
        deletedBoardStage = new Stage(StageStyle.UNDECORATED);
        deletedBoardStage.setScene(new Scene(addDeleteBoard.getValue()));
        deletedBoardStage.setTitle("Deleted board");
        deletedBoardStage.initModality(Modality.APPLICATION_MODAL);
        deletedBoardStage.showAndWait();
    }

    public void closeDeletedBoard() {
        if (deletedBoardStage != null)
            deletedBoardStage.close();
    }

    public void showDeletedCard() {
        var addDeleteCard = viewFactory.createCardDeleted();
        deletedCardStage = new Stage(StageStyle.UNDECORATED);
        deletedCardStage.setScene(new Scene(addDeleteCard.getValue()));
        deletedCardStage.setTitle("Deleted card");
        deletedCardStage.initModality(Modality.APPLICATION_MODAL);
        deletedCardStage.showAndWait();
    }

    public void closeDeletedCard() {
        if (deletedCardStage != null)
            deletedCardStage.close();
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void addBoard(Board board) {
        boards.add(board);
    }

    public List<Board> getBoards() {
        return boards;
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
    }
}
