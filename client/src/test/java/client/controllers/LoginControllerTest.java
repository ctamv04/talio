package client.controllers;

import client.Main;
import client.utils.ServerUtils;
import client.views.MyModule;
import client.views.ViewFactory;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    //create testing for the login controller
    private LoginController loginController;
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
    public void constructorTest() { }
}