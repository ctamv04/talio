package client.views;

import client.controllers.BoardController;
import client.controllers.MainCtrl;
import client.controllers.StartingController;
import client.controllers.TaskListController;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;

public class ViewFactory {
    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static Pair<BoardController, Parent> createBoard(Long boardId){
        return FXML.load(BoardController.class, "/client/board.fxml",boardId);
    }

    public static Pair<StartingController, Parent> createStarting(){
        return FXML.load(StartingController.class, "/client/starting.fxml");
    }

    public static Pair<TaskListController, Parent> createTaskList(Long id) {
        return FXML.load(TaskListController.class, "/client/taskList.fxml");
    }

    public static MainCtrl createMainCtrl() {
        return INJECTOR.getInstance(MainCtrl.class);
    }
}
