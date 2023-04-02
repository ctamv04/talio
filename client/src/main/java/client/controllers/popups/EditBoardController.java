package client.controllers.popups;

import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import models.Board;

import java.net.URL;
import java.util.ResourceBundle;

public class EditBoardController implements Initializable{
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Board board;
    @FXML
    private TextField board_name;
    @FXML
    private Button done_button;
    @FXML
    private Button back_button;
    @FXML
    private ColorPicker backgroundColor;
    @FXML
    private  ColorPicker textColor;

    @Inject
    public EditBoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
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
        backgroundColor.setValue(Color.valueOf(board.getBackgroundColor()));
        textColor.setValue(Color.valueOf(board.getFontColor()));
    }

    /**
     * Closes the edit board popup.
     */
    public void back() {
        mainCtrl.closeEditBoard();
    }

    /**
     * Saves the changes to the board.
     */
    public void save() {
        String name = board.getName();
        if (!board_name.getText().isBlank()) {
            name = board_name.getText();
        }
        board.setName(name);

        board.setBackgroundColor(backgroundColor.getValue().toString());
        board.setFontColor(textColor.getValue().toString());

        serverUtils.updateBoard(board.getId(), board);

        back();
    }


}
