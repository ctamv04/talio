package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.TaskCard;

import javax.swing.text.html.parser.Entity;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExtendedCardController implements Initializable{

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long task_id;
    private TaskCard card;
    @FXML
    private Label taskName;
    @FXML
    private Button backButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextArea desc_box;
    @FXML
    private Button addSub;
    @FXML
    private Button addTag;
    @FXML
    private ListView tagList;
    @FXML
    private ListView subList;
    @FXML
    private TextField editTitle2;

    @Inject
    public ExtendedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long task_id) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.task_id = task_id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            card = serverUtils.getTaskCard(task_id);
        } catch (Exception e){ //todo make personalized exception
            e.printStackTrace();
        }

        taskName.setText(card.getName());
        desc_box.setText(card.getDescription());
    }

    public void back() {
        mainCtrl.closeCard();
    }

    public void save() {
        if(!editTitle2.getText().isBlank())
            card.setName(editTitle2.getText());

        if(!desc_box.getText().equals(card.getDescription()))
            card.setDescription(desc_box.getText());

        serverUtils.updateTaskCard(card.getId(), card);

        back();
    }

    public void titleHoverIn() {
        editTitle2.setText(taskName.getText());
        editTitle2.setOpacity(1);
        taskName.setOpacity(0);
    }

    public void titleHoverOut() {
        editTitle2.setOpacity(0);
        taskName.setOpacity(1);
        taskName.setText(editTitle2.getText());
    }

}
