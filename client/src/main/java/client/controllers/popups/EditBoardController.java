package client.controllers.popups;

import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import models.Board;
import models.Tag;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EditBoardController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Board board;
    private final HashMap<Long, Tag> tags;
    private final Set<Tag> deletedTags;

    @FXML
    private TextField board_name;
    @FXML
    private Button done_button;
    @FXML
    private Button back_button;
    @FXML
    private Button add_tag_button;
    @FXML
    private Button edit_tag_button;
    @FXML
    private Button delete_tag_button;
    @FXML
    private ColorPicker backgroundColor;
    @FXML
    private ColorPicker textColor;
    @FXML
    private ListView<Tag> tags_overview;

    @Inject
    public EditBoardController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
        this.tags = new HashMap<>();
        deletedTags = new HashSet<>();
        tags.putAll(board.getTags().stream().collect(Collectors.toMap(Tag::getId, Function.identity())));
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backgroundColor.setValue(Color.valueOf(board.getBackgroundColor()));
        textColor.setValue(Color.valueOf(board.getFontColor()));
        add_tag_button.setOnMouseClicked(event -> {
            mainCtrl.showUpdateTagPage(this, null);
            setTagsOverview();
        });
        edit_tag_button.setOnMouseClicked(event -> {
            mainCtrl.showUpdateTagPage(this, tags_overview.getSelectionModel().getSelectedItem());
            setTagsOverview();
        });
        delete_tag_button.setOnMouseClicked(event -> onDelete());
        setTagsOverview();
    }

    /**
     * Changes tags overview. Need to be called not only from initialize method
     */
    public void setTagsOverview() {
        tags_overview.setItems(FXCollections.observableArrayList(tags.values()));
        tags_overview.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Tag> call(ListView<Tag> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Tag item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        Label label = new Label(item.getName());
                        label.setStyle("-fx-text-fill: " + item.getColor());
                        setGraphic(label);
                    }
                };
            }
        });
    }

    /**
     * Returns map of current tags
     *
     * @return tags
     */
    public HashMap<Long, Tag> getTags() {
        return tags;
    }

    /**
     * Called when delete button is pressed
     */
    public void onDelete() {
        Tag tag = tags_overview.getSelectionModel().getSelectedItem();
        tags.remove(tag.getId());
        deletedTags.add(tag);
        setTagsOverview();
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
        List<Long> initTags = board.getTags().stream().map(Tag::getId).collect(Collectors.toList());
        String name = board.getName();
        if (!board_name.getText().isBlank()) {
            name = board_name.getText();
        }
        board.setName(name);

        board.setBackgroundColor(backgroundColor.getValue().toString());
        board.setFontColor(textColor.getValue().toString());
        board.setTags(new ArrayList<>());

        for (Long key : tags.keySet()) {
            Tag tag = tags.get(key);

            if (initTags.contains(tag.getId())) {
                tag.setId(key);
                board.getTags().add(serverUtils.updateTag(key, tag));

            } else {
                tag.setId(null);
                board.getTags().add(serverUtils.addTag(tag, board.getId()));
            }
        }

        for (Tag tag : deletedTags) {
            serverUtils.deleteTag(tag.getId());
        }

        serverUtils.updateBoard(board.getId(), board);

        back();
    }


}
