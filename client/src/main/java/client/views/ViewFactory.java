package client.views;

import client.controllers.*;
import client.controllers.popups.*;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.util.Pair;
import models.Board;

public class ViewFactory {
    private final MyFXML FXML;

    public ViewFactory(Injector injector) {
        FXML = new MyFXML(injector);
    }

    public Pair<BoardController, Parent> createBoard(Board board) {
        return FXML.load(BoardController.class, "/client/board.fxml", board);
    }

    public Pair<StartingPageController, Parent> createStartingPage() {
        return FXML.load(StartingPageController.class, "/client/startingPage.fxml");
    }

    public Pair<ClientMenuController, Parent> createClientMenu(Board board, BoardController boardController) {
        return FXML.load(ClientMenuController.class, "/client/clientMenu.fxml", board, boardController);
    }

    public Pair<AddBoardController, Parent> createAddBoard() {
        return FXML.load(AddBoardController.class, "/client/addBoard.fxml");
    }

    public Pair<EditBoardController, Parent> createEditBoard(Board board) {
        return FXML.load(EditBoardController.class, "/client/editBoard.fxml", board);
    }

    public Pair<LoginController, Parent> createLogin() {
        return FXML.load(LoginController.class, "/client/loginPage.fxml");
    }

    public Pair<ClientOverviewController, Parent> createClientOverview(Board board) {
        return FXML.load(ClientOverviewController.class, "/client/clientOverview.fxml", board);
    }

    public Pair<TaskListController, Parent> createTaskList(Long taskListId, BoardController boardController) {
        return FXML.load(TaskListController.class, "/client/taskList.fxml", taskListId, boardController);
    }

    public Pair<AdminLoginController, Parent> createAdminLogin() {
        return FXML.load(AdminLoginController.class, "/client/adminLogin.fxml");
    }

    /**
     * Returns the beans / instances necessary to create a new ExtendedTaskCard scene for a specific TaskCard
     *
     * @param cardID TaskCard ID
     * @return Pair of ExtendedCardController bean, JavaFX Parent instance
     */
    public Pair<ExtendedCardController, Parent> createCard(Long cardID) {
        return FXML.load(ExtendedCardController.class, "/client/card.fxml", cardID);
    }

    public Pair<MinimizedCardController, Parent> createMinimizedCard(Long card_id) {
        return FXML.load(MinimizedCardController.class, "/client/minimizedCard.fxml", card_id);
    }

    public Pair<AddTaskListController, Parent> createAddTaskList(Long board_id) {
        return FXML.load(AddTaskListController.class, "/client/addTaskList.fxml", board_id);
    }

    public Pair<BoardDeletedController, Parent> createBoardDeleted() {
        return FXML.load(BoardDeletedController.class, "/client/boardDeleted.fxml");
    }
}
