package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class StartingPageController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField port_input;
    @FXML
    private Button done_button;
    @FXML
    private Button test_button;
    @FXML
    private Text success_message;
    @FXML
    private Text fail_message;

    @Inject
    public StartingPageController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        success_message.setVisible(false);
        fail_message.setVisible(false);
        done_button.setOnMouseClicked(event -> validate());
        test_button.setOnMouseClicked(event -> healthCheck());
    }

    public void validate() {
        if (port_input.getText().isBlank()) {
            mainCtrl.showLoginPage("8080");
        } else {
            mainCtrl.showLoginPage(port_input.getText());
        }
    }

    public void healthCheck() {
        String port;
        if (port_input.getText().isBlank()) {
            port = "8080";
        } else {
            port = port_input.getText();
        }

        if (serverUtils.healthCheck(port)) {
            success_message.setVisible(true);
            fail_message.setVisible(false);
        } else {
            fail_message.setVisible(true);
            success_message.setVisible(false);
        }
    }
}
