package client.controllers;

import client.utils.ServerUtils;
import client.views.ViewFactory;
import com.google.inject.Injector;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class MainPageControllerTest {

    //create testing for the login controller
    private MainPageController mainPageController;
    private Injector injector;
    private ViewFactory viewFactory;
    private MainCtrl mainCtrl;
    private ServerUtils serverUtils;
    private Stage primaryStage;
    private Pane pane;
    private Scene scene;

    @BeforeEach
    void setUp() throws IOException {
//        injector = createInjector(new MyModule());
//        viewFactory = new ViewFactory(injector);
//        mainCtrl = injector.getInstance(MainCtrl.class);
//        serverUtils = injector.getInstance(ServerUtils.class);
//        primaryStage = new Stage();
//        pane = new Pane();
//        scene = new Scene(pane);
//        primaryStage.setScene(scene);
//        loginController = new LoginController(serverUtils, mainCtrl);
//        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/login.fxml"));
//        loader.setController(loginController);
//        loader.load();
    }

    @Test
    void testInitialize() {
//        loginController.initialize(null, null);
    }

    @Test
    public void constructorTest() {
    }
}
