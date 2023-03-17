package client.views;

import client.controllers.BoardController;
import client.controllers.CardController;
import client.controllers.MainCtrl;
import client.controllers.StartingController;
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
//        return FXML.load(StartingController.class, "/client/starting.fxml");
        return FXML.load(StartingController.class, "/client/card.fxml");
    }

    public static MainCtrl createMainCtrl() {
        return INJECTOR.getInstance(MainCtrl.class);
    }

//    public static Pair<CardController, Parent> createCard(){
//        return FXML.load(CardController.class, "/client/card.fxml");
//    }
}
