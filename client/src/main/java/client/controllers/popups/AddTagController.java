package client.controllers.popups;

import client.utils.ExtendedCardUtils;
import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import models.Board;
import models.Tag;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTagController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final ExtendedCardUtils utils;
    private final Board board;

    @FXML
    private TextField tag_name_input;
    @FXML
    private Button save_button;
    @FXML
    private Button cancel_button;
    @FXML
    private ColorPicker color;

    @Inject
    public AddTagController(ServerUtils serverUtils, MainCtrl mainCtrl, ExtendedCardUtils utils, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.utils = utils;
        this.board = board;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel_button.setOnMouseClicked(event -> mainCtrl.closeAddTagPage());
        save_button.setOnMouseClicked(event -> save());
    }

    public void save() {
        Tag tag = new Tag();
        tag.setBoard(board);
        if (!tag_name_input.getText().isBlank()) {
            tag.setName(tag_name_input.getText());
        }

        tag.setColor(utils.colorConverter(color.getValue()));
        serverUtils.addTag(tag, board.getId());
        mainCtrl.closeAddTagPage();
    }
}
