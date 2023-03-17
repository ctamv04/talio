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

    public static Pair<MenuController, Parent> createMenu() {
        return FXML.load(MenuController.class, "/client/menu.fxml");
    }

    public static Pair<AddBoardController, Parent> createAddBoard() {
        return FXML.load(AddBoardController.class, "/client/addBoard.fxml");
    }

    public static Pair<StartingController, Parent> createStarting() {
        return FXML.load(StartingController.class, "/client/starting.fxml");
    }

    public static Pair<TaskListController, Parent> createTaskList(Long id) {
        return FXML.load(TaskListController.class, "/client/taskList.fxml");
    }

    public static MainCtrl createMainCtrl() {
        return INJECTOR.getInstance(MainCtrl.class);
    }
}
