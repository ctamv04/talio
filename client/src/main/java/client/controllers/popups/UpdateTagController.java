package client.controllers.popups;

import client.controllers.MainCtrl;
import client.utils.ExtendedCardUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import models.Tag;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class UpdateTagController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final ExtendedCardUtils utils;
    private final EditBoardController editBoardController;
    private Tag tag;

    @FXML
    private TextField tag_name_input;
    @FXML
    private Button save_button;
    @FXML
    private Button cancel_button;
    @FXML
    private ColorPicker color;
    @FXML
    private Text text;

    @Inject
    public UpdateTagController(ServerUtils serverUtils, MainCtrl mainCtrl, ExtendedCardUtils utils,
                               EditBoardController editBoardController,
                               Tag tag) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.utils = utils;
        this.editBoardController = editBoardController;
        this.tag = tag;
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel_button.setOnMouseClicked(event -> mainCtrl.closeAddTagPage());
        save_button.setOnMouseClicked(event -> save());

        if (tag != null) {
            text.setText("Edit tag");
            color.setValue(Color.valueOf(tag.getColor()));
            tag_name_input.setText(tag.getName());
        }
    }

    /**
     * Saves changes made to the tag. Creates new tag or updates tag that already exists
     */
    public void save() {
        Tag tag = new Tag();
        if (!tag_name_input.getText().isBlank()) {
            tag.setName(tag_name_input.getText());
        }

        tag.setColor(utils.colorConverter(color.getValue()));
        if (this.tag != null) {
            tag.setId(this.tag.getId());
            editBoardController.getTags().put(this.tag.getId(), tag);
        } else {
            Long id = 1L;
            if (!editBoardController.getTags().isEmpty()) {
                id += Collections.max(editBoardController.getTags().keySet());
            }
            
            tag.setId(id);
            editBoardController.getTags().put(id, tag);
        }

        mainCtrl.closeAddTagPage();
    }
}
