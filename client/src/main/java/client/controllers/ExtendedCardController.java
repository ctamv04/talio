package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import models.TaskCard;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


import java.net.URL;
import java.util.*;


public class ExtendedCardController implements Initializable{

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long task_id;
    private TaskCard card;
    private Map<String, Boolean> tempSubs = new HashMap<>();
    private boolean editFlag = false;
    @FXML
    private Label taskName;
    @FXML
    private Label descLabel;
    @FXML
    private Label subLabel;
    @FXML
    private Label tagsLabel;
    @FXML
    private Label backLabel;
    @FXML
    private Label fontLabel;
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
    @FXML
    private ListView subs;
    @FXML
    private AnchorPane window;
    @FXML
    private ColorPicker color_back;
    @FXML
    private ColorPicker color_font;

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
     * Handles stage initialization, populating the Subtask, Tag lists, and serves most user actions
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

        window.setStyle("-fx-background-color: " + card.getBackID() + ";");
        taskName.setStyle("-fx-text-fill: " + card.getFontID() + ";");
        descLabel.setStyle("-fx-text-fill: " + card.getFontID() + ";");
        subLabel.setStyle("-fx-text-fill: " + card.getFontID() + ";");
        tagsLabel.setStyle("-fx-text-fill: " + card.getFontID() + ";");
        backLabel.setStyle("-fx-text-fill: " + card.getFontID() + ";");
        fontLabel.setStyle("-fx-text-fill: " + card.getFontID() + ";");


        taskName.setText(card.getName());
        desc_box.setText(card.getDescription());

        newSubBox.toBack();
        tempSubs = card.getSubs();

        if(tempSubs != null || tempSubs.isEmpty()){
            List<CheckBox> graphic = new ArrayList<>();
            tempSubs.forEach((a,b) -> {

                CheckBox box = new CheckBox(a);
                box.setSelected(b);

                box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        var key = box.getText();
                        var value = box.isSelected();
                        tempSubs.replace(key, value);
                    }
                });

                graphic.add(box);
            });

            subs.setItems(FXCollections.observableArrayList(graphic));
        }

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

                if(editFlag && !newSub.getText().isBlank() && !tempSubs.containsKey(newSub.getText())){

                    var old_key = ((CheckBox) subs.getSelectionModel().getSelectedItem()).getText();
                    var old_value = ((CheckBox) subs.getSelectionModel().getSelectedItem()).isSelected();

                    tempSubs.remove(old_key);
                    tempSubs.put(newSub.getText(), old_value);

                    subs.getItems().remove(subs.getSelectionModel().getSelectedItem());
                    CheckBox editedBox = new CheckBox(newSub.getText());
                    editedBox.setSelected(old_value);
                    subs.getItems().add(editedBox);

                    editFlag = false;

                }else if(!newSub.getText().isBlank() && !tempSubs.containsKey(newSub.getText())){
                    tempSubs.put(newSub.getText(), false);

                    CheckBox newBox = new CheckBox(newSub.getText());
                    newBox.setSelected(false);
                    subs.getItems().add(newBox);
                }

                newSubBox.setOpacity(0);
                newSubBox.toBack();
            }
        });

        subs.setOnMouseClicked(event -> {

            if(event.getClickCount() == 2){

                editFlag = true;
                newSubBox.setOpacity(1);
                newSubBox.toFront();
                newSub.requestFocus();
            }
        });

        subs.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.DELETE){

                tempSubs.remove(((CheckBox) subs.getSelectionModel().getSelectedItem()).getText());
                subs.getItems().remove(subs.getSelectionModel().getSelectedItem());
            }
        });

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

    /**
     * Changes the background colour of the ExtendedTaskCard
     */
    public void backChange() {

        String color = colorConverter(color_back.getValue());
        window.setStyle("-fx-background-color: " + color + ";");
        card.setBackID(color);

    }

    /**
     * Changes the font colour of the ExtendedTaskCard
     */
    public void fontChange() {

        String color = colorConverter(color_font.getValue());
        taskName.setStyle("-fx-text-fill: " + color + ";");
        descLabel.setStyle("-fx-text-fill: " + color + ";");
        subLabel.setStyle("-fx-text-fill: " + color + ";");
        tagsLabel.setStyle("-fx-text-fill: " + color + ";");
        backLabel.setStyle("-fx-text-fill: " + color + ";");
        fontLabel.setStyle("-fx-text-fill: " + color + ";");
        card.setFontID(color);
    }

    /**
     * Method for converting Java Color classes to CSS compatible hex colour codes
     *
     * @param color input Color object
     * @return String containing hex colour code
     */
    public String colorConverter(Color color){

        //credit: http://www.java2s.com/example/java/javafx/javafx-color-to-css-color.html

        return "rgba(" + Math.round(255 * color.getRed()) + ","
                + Math.round(255 * color.getGreen()) + ","
                + Math.round(255 * color.getBlue()) + ","
                + color.getOpacity() + ")";
    }

}
