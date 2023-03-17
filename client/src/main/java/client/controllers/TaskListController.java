package client.controllers;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.TaskList;

import java.net.URL;
import java.util.ResourceBundle;

public class TaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    private Long id;

    @Inject
    public TaskListController(ServerUtils serverUtils, MainCtrl mainCtrl, long id) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.id = id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
