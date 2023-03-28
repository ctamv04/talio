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

    @Inject
    public AdminLoginController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        done_button.setOnMouseClicked(event -> validate());
        cancel_button.setOnMouseClicked(event -> mainCtrl.closeAdminLogin());
        connection_denied_message.setVisible(false);
    }

    private void validate() {
        if (password_input.getText().isBlank()) {
            connection_denied_message.setVisible(true);
        } else {
            if (password_input.getText().equals(serverUtils.getPassword())) {
                mainCtrl.closeAdminLogin();
            } else {
                connection_denied_message.setVisible(true);
            }
        }
    }
}
