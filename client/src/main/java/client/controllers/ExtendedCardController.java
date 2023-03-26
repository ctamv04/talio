package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.Pair;
import models.TaskCard;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;


import java.net.URL;
import java.util.*;


public class ExtendedCardController implements Initializable{

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long task_id;
    private TaskCard card;
    private Map<String, Boolean> tempSubs = new HashMap<>();

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
    private TextField editTitle2;
    @FXML
    private TextField newSub;
    @FXML
    private HBox newSubBox;
    @FXML
    private Label cancelNew;

    /**
     *
     * @param serverUtils
     * @param mainCtrl
     * @param task_id
     */
    @Inject
    public ExtendedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long task_id) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.task_id = task_id;
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

        try{
            card = serverUtils.getTaskCard(task_id);
        } catch (Exception e){
            e.printStackTrace();
        }

        taskName.setText(card.getName());
        desc_box.setText(card.getDescription());

        newSubBox.toBack();
        tempSubs = card.getSubs();

        addSub.setOnMouseClicked(event -> {
            newSub.clear();
            newSubBox.setOpacity(1);
            newSubBox.toFront();
            newSub.requestFocus();
        });

        cancelNew.setOnMouseClicked(event -> {
            newSubBox.setOpacity(0);
            newSubBox.toBack();
        });

        newSub.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                if (!newSub.getText().isBlank()) {
                    tempSubs.put(newSub.getText(), false);
                }

                newSubBox.setOpacity(0);
                newSubBox.toBack();
            }
        });

//        window.setOnMouseClicked(event -> {
//
//            if(event.getTarget().getClass() == CheckBox.class){
//
//                var key = ((CheckBox) subs.getSelectionModel().getSelectedItem()).getText();
//                if(key != null){
//
//                    ((CheckBox) subs.getSelectionModel().getSelectedItem()).fire();
//                    var value = ((CheckBox) subs.getSelectionModel().getSelectedItem()).isSelected();
//                    tempSubs.replace(key, value);
//                }
//            }
//        });

    }

    /**
     *Closes the respective ExtendedTaskCard scene
     */
    public void back() {
        mainCtrl.closeCard();
    }

    /**
     *Saves the new information in the TaskCard
     */
    public void save() {
        if(!editTitle2.getText().isBlank())
            card.setName(editTitle2.getText());

        if(!desc_box.getText().equals(card.getDescription()))
            card.setDescription(desc_box.getText());

//        var updatedSubs = card.getSubs();
//        temp.forEach((a,b) -> upsubs.put(a,b));
        card.setSubs(tempSubs);

        serverUtils.updateTaskCard(card.getId(), card);

        back();
    }

    /**
     *Allows the user to edit the title of the TaskCard by hovering on its title area
     */
    public void titleHoverIn() {
        editTitle2.setText(taskName.getText());
        editTitle2.setOpacity(1);
        taskName.setOpacity(0);
    }


    /**
     *Saves the new title when the user hovers out of the TaskCard's title area
     */
    public void titleHoverOut() {
        editTitle2.setOpacity(0);
        taskName.setOpacity(1);
        taskName.setText(editTitle2.getText());
    }

}
