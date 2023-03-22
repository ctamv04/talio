package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.TaskList;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField tasklist_name;
    @FXML
    private Button done_button;
    @FXML
    private Button back_button;
    private final Long boardId;

    @Inject
    public AddTaskListController (ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        done_button.setOnMouseClicked(event -> save());
        back_button.setOnMouseClicked(event -> back());
    }

    public void save() {
        String name = "Untitled Task List";
        if (!tasklist_name.getText().isBlank()) {
            name = tasklist_name.getText();
        }
        TaskList taskList = new TaskList(name, serverUtils.getBoard(boardId));
        serverUtils.addTaskList(taskList, boardId);
        back();
    }

    public void back() {
        mainCtrl.closeAddTaskListPage();
    }
}
