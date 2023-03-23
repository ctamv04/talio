package client.views;

import client.controllers.*;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.util.Pair;

public class ViewFactory {
    private final MyFXML FXML;

    public ViewFactory(Injector injector) {
        FXML = new MyFXML(injector);
    }

    public Pair<BoardController, Parent> createBoard(String port, Long boardId) {
        return FXML.load(BoardController.class, "/client/board.fxml", port, boardId);
    }

    public Pair<StartingPageController, Parent> createStartingPage() {
        return FXML.load(StartingPageController.class, "/client/startingPage.fxml");
    }

    public Pair<ClientMenuController, Parent> createClientMenu(String port) {
        return FXML.load(ClientMenuController.class, "/client/clientMenu.fxml", port);
    }

    public Pair<AddBoardController, Parent> createAddBoard(String port) {
        return FXML.load(AddBoardController.class, "/client/addBoard.fxml", port);
    }

    public Pair<ExtendedCardController, Parent> createLogin(String port) {
        return FXML.load(ExtendedCardController.class, "/client/loginPage.fxml", port);
    }

    public Pair<ClientOverviewController, Parent> createClientOverview(String port, Long boardId) {
        return FXML.load(ClientOverviewController.class, "/client/clientOverview.fxml", port, boardId);
    }

    public Pair<TaskListController, Parent> createTaskList(String port, Long id) {
        return FXML.load(TaskListController.class, "/client/taskList.fxml", port, id);
    }

    public Pair<ExtendedCardController, Parent> createCard(String port, Long card_id) {
        return FXML.load(ExtendedCardController.class, "/client/card.fxml", port, card_id);
    }

    public Pair<MinimizedCardController, Parent> createMinimizedCard(String port, Long card_id) {
        return FXML.load(MinimizedCardController.class, "/client/minimizedCard.fxml", port, card_id);
    }

    public Pair<AddTaskListController, Parent> createAddTaskList(String port, Long board_id) {
        return FXML.load(AddTaskListController.class, "/client/addTaskList.fxml", port, board_id);
    }
}
