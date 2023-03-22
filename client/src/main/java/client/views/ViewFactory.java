package client.views;

import client.controllers.*;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;

public class ViewFactory {
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static Pair<BoardController, Parent> createBoard(Long boardId) {
        return FXML.load(BoardController.class, "/client/board.fxml", boardId);
    }

    public static Pair<ClientMenuController, Parent> createClientMenu() {
        return FXML.load(ClientMenuController.class, "/client/clientMenu.fxml");
    }

    public static Pair<AddBoardController, Parent> createAddBoard() {
        return FXML.load(AddBoardController.class, "/client/addBoard.fxml");
    }

    public static Pair<ExtendedCardController, Parent> createLogin() {
        return FXML.load(ExtendedCardController.class, "/client/loginPage.fxml");
    }

    public static Pair<ClientOverviewController, Parent> createClientOverview(Long boardId) {
        return FXML.load(ClientOverviewController.class, "/client/clientOverview.fxml", boardId);
    }

    public static Pair<TaskListController, Parent> createTaskList(Long id) {
        return FXML.load(TaskListController.class, "/client/taskList.fxml", id);
    }

    public static MainCtrl createMainCtrl() {
        return INJECTOR.getInstance(MainCtrl.class);
    }

    public static Pair<ExtendedCardController, Parent> createCard(Long card_id) {
        return FXML.load(ExtendedCardController.class, "/client/card.fxml", card_id);
    }

    public static Pair<MinimizedCardController, Parent> createMinimizedCard(Long card_id){
        return FXML.load(MinimizedCardController.class, "/client/minimizedCard.fxml", card_id);
    }

    public static Pair<AddTaskListController, Parent> addTaskList(Long taskList_id) {
        return FXML.load(AddTaskListController.class, "/client/addTaskList.fxml", taskList_id);
    }
}
