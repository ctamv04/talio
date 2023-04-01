package client.controllers;

import client.utils.ServerUtils;
import client.utils.WebsocketUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import models.Tag;
import models.TaskCard;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.*;

public class ExtendedCardController implements Initializable{

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long task_id;
    private final WebsocketUtils websocketUtils;
    private TaskCard card;
    private Map<String, Boolean> tempSubs = new HashMap<>();
    private Set<Tag> taskTags = new HashSet<>();
    private Set<Tag> boardTags = new HashSet<>();
    ExtendedCardUtils utils;
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
    private FontAwesomeIconView backButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextArea desc_box;
    @FXML
    private Button addSub;
    @FXML
    private Button addTag;
    @FXML
    private ListView<HBox> tagList;
    @FXML
    private TextField editTitle2;
    @FXML
    private TextField newSub;
    @FXML
    private HBox newSubBox;
    @FXML
    private FontAwesomeIconView cancelNew;
    @FXML
    private ListView<HBox> subs;
    @FXML
    private AnchorPane window;
    @FXML
    private ColorPicker color_back;
    @FXML
    private ColorPicker color_font;
    @FXML
    private ListView<Tag> bTagList;
    @FXML
    private FontAwesomeIconView cancelbTagList;
    @FXML
    private FontAwesomeIconView icon;
    @FXML
    private HBox bTagListBox;

    /**
     *
     * @param serverUtils
     * @param mainCtrl
     * @param task_id
     */
    @Inject
    public ExtendedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long task_id, WebsocketUtils websocketUtils, ExtendedCardUtils utils) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.task_id = task_id;
        this.websocketUtils = websocketUtils;
        this.utils = utils;
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

        color_back.setValue(Color.web(card.getBackID()));
        color_font.setValue(Color.web(card.getFontID()));
        fontCustomization(card.getFontID());
        backCustomization(card.getBackID());

        taskName.setText(card.getName());
        desc_box.setText(card.getDescription());
        editTitle2.setText(taskName.getText());

        tagList.setOrientation(Orientation.HORIZONTAL);
        bTagListBox.toBack();

        newSubBox.setOpacity(0);

        tempSubs = card.getSubs();
        boardTags = serverUtils.getBoardTags(card.getId());
        taskTags = card.getTags();

        bTagList.setItems(FXCollections.observableArrayList(boardTags));
        bTagList.setCellFactory(a -> new ListCell<Tag>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                super.updateItem(tag, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(tag.getName());
                    setStyle("-fx-text-fill: " + tag.getColor());
                }
            }
        });

        bTagList.setOnMouseClicked(event -> {

            var tag = (Tag) bTagList.getSelectionModel().getSelectedItem();
            if(tag != null && !taskTags.contains(tag)){

                taskTags.add(tag);

                tagList.getItems().add(tagEntryFactory(tag));

                bTagListBox.setOpacity(0);
                bTagListBox.toBack();
            }

        });

        if(taskTags != null && !taskTags.isEmpty()) {
            List<HBox> graphic = new ArrayList<>();
            taskTags.forEach((tag) -> {

                HBox tagBox = tagEntryFactory(tag);

                graphic.add(tagBox);
            });

            tagList.setItems(FXCollections.observableArrayList(graphic));
        }

        if(tempSubs != null || !tempSubs.isEmpty()){
            List<HBox> graphic = new ArrayList<>();
            tempSubs.forEach((a,b) -> {

                HBox tagBox = entryFactory(a, b);

                graphic.add(tagBox);
            });

            subs.setItems(FXCollections.observableArrayList(graphic));
        }

        addSub.setOnMouseClicked(event -> {
            newSub.clear();
            newSubBox.setOpacity(1);
            newSub.requestFocus();
        });

        cancelNew.setOnMouseClicked(event -> {
            newSubBox.setOpacity(0);
        });

        newSub.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {

                editOrSave();
                newSubBox.setOpacity(0);
            }
        });

        addTag.setOnMouseClicked(event -> {
            bTagListBox.setOpacity(1);
            bTagListBox.toFront();
        });

        cancelbTagList.setOnMouseClicked(event -> {
            bTagListBox.setOpacity(0);
            bTagListBox.toBack();
        });

        startWebsockets();
    }

    private void startWebsockets() {

        websocketUtils.registerForMessages("/topic/extended-taskcard/"+task_id, TaskCard.class, updatedTaskCard->{
            Platform.runLater(()->{

                if(updatedTaskCard.getPosition()==-1){
                    mainCtrl.closeCard();
                    stopWebsockets();
                }
            });
        });
    }

    private void stopWebsockets(){

        websocketUtils.unsubscribeFromMessages("/topic/extended-taskcard/"+task_id);
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

        utils.save(editTitle2.getText(), desc_box.getText(), tempSubs, taskTags, card);
        back();
    }

    /**
     *Allows the user to edit the title of the TaskCard by hovering on its title area
     */
    public void titleHoverIn() {

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

        String color = utils.colorConverter(color_back.getValue());
        backCustomization(color);
        card.setBackID(color);

    }

    /**
     * Changes the font colour of the ExtendedTaskCard
     */
    public void fontChange() {

        String color = utils.colorConverter(color_font.getValue());
        fontCustomization(color);
        card.setFontID(color);
    }

    public HBox entryFactory(String a, Boolean b){

        FontAwesomeIconView edit = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        FontAwesomeIconView delete = new FontAwesomeIconView(FontAwesomeIcon.TIMES);

        CheckBox checkBox = new CheckBox(a);
        checkBox.setSelected(b);

        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                var key = checkBox.getText();
                var value = checkBox.isSelected();
                tempSubs.replace(key, value);
            }
        });

        edit.setOnMouseClicked(event -> {
            editFlag = true;
            newSub.setText(((CheckBox) subs.getSelectionModel().getSelectedItem().getChildren().get(0)).getText());
            newSubBox.setOpacity(1);
            newSub.requestFocus();
        });

        delete.setOnMouseClicked(event -> {
            tempSubs.remove(a);
            subs.getItems().remove(subs.getSelectionModel().getSelectedItem());
        });

        HBox box = new HBox(checkBox, edit, region, delete);
        box.setStyle("-fx-spacing: 5px");

        return box;
    }

    public HBox tagEntryFactory(Tag tag){

        Label label = new Label(tag.getName());
        label.setStyle("-fx-text-fill: " + tag.getColor());

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        FontAwesomeIconView delete = new FontAwesomeIconView(FontAwesomeIcon.TIMES);

        delete.setOnMouseClicked(event -> {
            taskTags.remove(tag);
            tagList.getItems().remove(tagList.getSelectionModel().getSelectedItem());
        });

        HBox box = new HBox(label, region, delete);
        box.setStyle("-fx-spacing: 5px; -fx-border-color: " + tag.getColor());

        return box;
    }

    public void fontCustomization(String color){

        taskName.setStyle("-fx-text-fill: " + color + ";");
        descLabel.setStyle("-fx-text-fill: " + color + ";");
        subLabel.setStyle("-fx-text-fill: " + color + ";");
        tagsLabel.setStyle("-fx-text-fill: " + color + ";");
        backLabel.setStyle("-fx-text-fill: " + color + ";");
        fontLabel.setStyle("-fx-text-fill: " + color + ";");
        icon.setStyle("-fx-font-family: FontAwesome; -fx-fill: " + color + ";");
        backButton.setStyle("-fx-font-family: FontAwesome; -fx-fill: " + color + ";");
    }

    public void backCustomization(String color){

        window.setStyle("-fx-background-color: " + color + ";");
    }

    public void editOrSave(){
        if(editFlag && !newSub.getText().isBlank() && !tempSubs.containsKey(newSub.getText())){

            var index = subs.getSelectionModel().getSelectedIndex();
            var old_key = ((CheckBox) subs.getSelectionModel().getSelectedItem().getChildren().get(0)).getText();
            var old_value = ((CheckBox) subs.getSelectionModel().getSelectedItem().getChildren().get(0)).isSelected();

            tempSubs.remove(old_key);
            tempSubs.put(newSub.getText(), old_value);

            subs.getItems().remove(subs.getSelectionModel().getSelectedItem());
            subs.getItems().add(index, entryFactory(newSub.getText(), old_value));

            editFlag = false;

        }else if(!newSub.getText().isBlank() && !tempSubs.containsKey(newSub.getText())){
            tempSubs.put(newSub.getText(), false);

            subs.getItems().add(entryFactory(newSub.getText(), false));
        }
    }

}
