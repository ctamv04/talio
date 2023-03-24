package client.controllers;

import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @Mock
    private MainCtrl mainCtrl;

    @Mock
    private ServerUtils serverUtils;

    private LoginController loginController;

    @BeforeEach
    public void setUp() {
        mainCtrl = new MainCtrl();
        serverUtils = new ServerUtils();
        loginController = new LoginController(serverUtils, mainCtrl);
    }

    @Test
    public void constructorTest() { }
}