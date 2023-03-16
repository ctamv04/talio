package client.controllers;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long id;
    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, Long id) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.id = id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
