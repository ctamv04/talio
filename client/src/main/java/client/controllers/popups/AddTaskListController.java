package client.controllers.popups;

import client.controllers.LoginController;
import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.TaskList;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTaskListController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long boardId;
    @FXML
    private TextField tasklist_name;
    @FXML
    private Button done_button;
    @FXML
    private Button back_button;

    /**
     * Constructor for the AddTaskListController
     * @param serverUtils serverUtils
     * @param mainCtrl the main Controller
     * @param boardId the ID of the board were the TL will be added
     */
    @Inject
    public AddTaskListController (ServerUtils serverUtils, MainCtrl mainCtrl, Long boardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
    }

    /**
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        done_button.setOnMouseClicked(event -> save());
        back_button.setOnMouseClicked(event -> back());
    }

    /**
     * Function that adds the TL to the board
     */
    public void save() {
        TaskList taskList = new TaskList();
        if (!tasklist_name.getText().isBlank()) {
            String name = tasklist_name.getText();
            taskList.setName(name);
        }
        serverUtils.addTaskList(taskList, boardId);
        back();
    }

    /**
     * Closes the add TL popup
     */
    public void back() {
        mainCtrl.closeAddTaskListPage();
    }
}
