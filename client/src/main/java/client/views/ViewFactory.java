package client.views;

import client.controllers.*;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.util.Pair;

public class ViewFactory {
    private final MyFXML FXML;

    public ViewFactory(Injector injector) {
        FXML=new MyFXML(injector);
    }

    public Pair<BoardController, Parent> createBoard(Long boardId) {
        return FXML.load(BoardController.class, "/client/board.fxml", boardId);
    }

    public Pair<ClientMenuController, Parent> createClientMenu() {
        return FXML.load(ClientMenuController.class, "/client/clientMenu.fxml");
    }

    public Pair<AddBoardController, Parent> createAddBoard() {
        return FXML.load(AddBoardController.class, "/client/addBoard.fxml");
    }

    public Pair<ExtendedCardController, Parent> createLogin() {
        return FXML.load(ExtendedCardController.class, "/client/loginPage.fxml");
    }

    public Pair<ClientOverviewController, Parent> createClientOverview(Long boardId) {
        return FXML.load(ClientOverviewController.class, "/client/clientOverview.fxml", boardId);
    }

    public Pair<TaskListController, Parent> createTaskList(Long id) {
        return FXML.load(TaskListController.class, "/client/taskList.fxml", id);
    }

    public Pair<ExtendedCardController, Parent> createCard(Long card_id) {
        return FXML.load(ExtendedCardController.class, "/client/card.fxml", card_id);
    }

    public Pair<MinimizedCardController, Parent> createMinimizedCard(Long card_id){
        return FXML.load(MinimizedCardController.class, "/client/minimizedCard.fxml", card_id);
    }

    public Pair<AddTaskListController, Parent> createAddTaskList(Long board_id) {
        return FXML.load(AddTaskListController.class, "/client/addTaskList.fxml", board_id);
    }
}
