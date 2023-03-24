package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import models.Board;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @FXML
    private Text invalid_text;
    @FXML
    private Button new_board_button;
    @FXML
    private Button join_board_button;
    @FXML
    private Button back_button;
    @FXML
    private TextField code_input;
    @FXML
    private ListView<Board> boards;
    @FXML
    private Button delBoard;
    @FXML
    private Button enterBoard;
    @FXML
    private VBox buttonBox;
    @FXML
    private AnchorPane window;

    /***
     * Constructor for LoginController
     * @param serverUtils contains the server and related methods
     * @param mainCtrl the main controller
     */
    @Inject
    public LoginController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /***
     * Initializes the login page
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

        buttonBox.setOpacity(0L);

        invalid_text.setVisible(false);
        boards.setItems(FXCollections.observableArrayList(serverUtils.getBoards()));
        boards.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Board> call(ListView<Board> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Board item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        setGraphic(new Label(item.getName()));
                    }
                };
            }
        });

        boards.setOnMouseClicked(event -> boardClicked(event));

        join_board_button.setOnAction(event -> joinButtonClicked());

        new_board_button.setOnAction(event -> mainCtrl.showAddBoardPage());

        back_button.setOnMouseClicked(event -> {
            serverUtils.setServer("http://localhost:8080/");
            mainCtrl.showStartingPage();
        });

        window.setOnMouseClicked(event -> {

            if (event.getTarget() != buttonBox && event.getTarget() != boards) {
                buttonBox.setOpacity(0L);
            }
        });
    } // initialize

    /***
     * This is called when a mouse event happens to a board.
     * @param event the mouse event that happened: clicked/clicked twice/...
     */
    public void boardClicked (MouseEvent event) {
        Board board = boards.getSelectionModel().getSelectedItem();

        if (board != null) {
            Long clickedBoardID = board.getId();

            if (event.getClickCount() == 2) {
                mainCtrl.showClientOverview(clickedBoardID);
            }

            buttonBox.setOpacity(1L);
            enterBoard.setOnMouseClicked(event2 -> {
                mainCtrl.showClientOverview(clickedBoardID);
            });

            delBoard.setOnMouseClicked(event2 -> {
                serverUtils.deleteBoard(clickedBoardID);
            });
        }
    } // boardClicked

    /***
     * This is called when the join button is clicked.
     */
    public void joinButtonClicked () {
        try {
            Long id = Long.parseLong(code_input.getText());
            Board board = serverUtils.getBoard(id);
            mainCtrl.showClientOverview(serverUtils.getBoard(id).getId());
        } catch (NumberFormatException | WebApplicationException e) {
            invalid_text.setVisible(true);
        }
    } // joinButtonClicked
} // LoginController

