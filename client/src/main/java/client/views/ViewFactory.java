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

    public static Pair<AddFirstBoardController, Parent> createAddFirstBoard() {
        return FXML.load(AddFirstBoardController.class, "/client/addFirstBoard.fxml");
    }

    public static Pair<StartingController, Parent> createStarting() {
        return FXML.load(StartingController.class, "/client/starting.fxml");
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
}
