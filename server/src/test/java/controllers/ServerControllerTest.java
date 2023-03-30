package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.controllers.ServerController;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerControllerTest {
    private ServerController serverController;

    @BeforeEach
    public void setup() {
        serverController = new ServerController();
    }

    @Test
    public void testGetPassword() {
        String password = serverController.getPassword();
        assertNotNull(password);
    }

    @Test
    public void testPasswordLength() {
        String password;
        for (int i = 0; i < 10; i++) {
            password = serverController.generatePassword();
            assertTrue(8 <= password.length() && password.length() <= 16);
        }
    }
}
