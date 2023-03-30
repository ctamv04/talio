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
    private TextField url_input;
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

    private String validate_url(String url) {
        if (url.startsWith("http")) {
            return url;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(url);
        return sb.toString();
    }

    public String getServer() {
        if (url_input.getText().isBlank()) {
            return "http://localhost:8080/";
        }
        return validate_url(url_input.getText());
    }

    public void validate() {
        String url = getServer();

        if (serverUtils.healthCheck(url)) {
            serverUtils.setServer(url);
            mainCtrl.showLoginPage();
        } else {
            fail_message.setVisible(true);
            success_message.setVisible(false);
        }
    }

    public void healthCheck() {
        String url = getServer();

        if (serverUtils.healthCheck(url)) {
            success_message.setVisible(true);
            fail_message.setVisible(false);
        } else {
            fail_message.setVisible(true);
            success_message.setVisible(false);
        }
    }
}
