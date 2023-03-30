package client.controllers.popups;

import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @FXML
    private Button done_button;
    @FXML
    private Button cancel_button;
    @FXML
    private Text connection_denied_message;
    @FXML
    private TextField password_input;

    /**
     * Constructor for AdminLoginController
     *
     * @param serverUtils serverUtils
     * @param mainCtrl    the main Controller
     */
    @Inject
    public AdminLoginController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        done_button.setOnMouseClicked(event -> validate());
        cancel_button.setOnMouseClicked(event -> mainCtrl.closeAdminLogin());
        connection_denied_message.setVisible(false);
    }

    /**
     * Called when done button is clicked.
     * Checks if password was inserted correctly
     */
    private void validate() {
        if (password_input.getText().isBlank()) {
            connection_denied_message.setVisible(true);
        } else {
            System.out.println(password_input.getText() + " " + password_input.getText().length());
            System.out.println(serverUtils.getPassword() + " " + serverUtils.getPassword().length());
            if (password_input.getText().equals(serverUtils.getPassword())) {
                mainCtrl.updateRole();
                mainCtrl.closeAdminLogin();
            } else {
                connection_denied_message.setVisible(true);
            }
        }
    }
}
