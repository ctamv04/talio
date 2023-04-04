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

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MainPageController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    private final String file_path;

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
    private ListView<Board> boards_view;
    @FXML
    private Button delBoard;
    @FXML
    private Button leaveBoard;
    @FXML
    private Button enterBoard;
    @FXML
    private VBox buttonBox;
    @FXML
    private AnchorPane window;
    @FXML
    private Button admin_login_button;
    @FXML

    private AnchorPane overlay;

    /***
     * Constructor for LoginController
     * @param serverUtils contains the server and related methods
     * @param mainCtrl the main controller
     */
    @Inject
    public MainPageController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.file_path = "../client/src/main/java/client/sessions_info/" + serverUtils.getAddress() + ".txt";
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
        if (mainCtrl.getBoards().isEmpty()) {
            try {
                loadState();
            } catch (FileNotFoundException e) {
                System.out.println("State for current session was not found");
            }
        }

        mainCtrl.getPrimaryStage().setOnCloseRequest(event -> {
            try {
                saveState();
            } catch (IOException e) {
                System.out.println("State was not saved");
            }
        });

        overlay.setVisible(false);
        buttonBox.setOpacity(0L);

        invalid_text.setVisible(false);

        setVisibleBoards();

        boards_view.setOnMouseClicked(this::boardClicked);

        join_board_button.setOnAction(event -> joinButtonClicked());

        new_board_button.setOnAction(event -> {
            overlay.setVisible(true);
            mainCtrl.showAddBoardPage();
            overlay.setVisible(false);
        });

        if (mainCtrl.getIsAdmin()) {
            admin_login_button.setVisible(false);
        } else {
            admin_login_button.setOnAction(event -> mainCtrl.showAdminLogin());
        }

        back_button.setOnMouseClicked(event -> {
            try {
                saveState();
            } catch (IOException e) {
                System.out.println("State was not saved");
            }
            mainCtrl.setIsAdmin(false);
            mainCtrl.getBoards().clear();
            serverUtils.setServer("http://localhost:8080/");
            mainCtrl.showLoginPage();
        });

        window.setOnMouseClicked(event -> {
            if (event.getTarget() != buttonBox && event.getTarget() != boards_view)
                buttonBox.setOpacity(0);

        });
    }

    /**
     * Adds boards to the login page.
     * Admin can see all boards in the current workspace,
     * user can see boards it created or accessed before.
     */
    private void setVisibleBoards() {
        if (mainCtrl.getIsAdmin()) {
            boards_view.setItems(FXCollections.observableArrayList(serverUtils.getBoards()));
        } else {
            boards_view.setItems(FXCollections.observableArrayList(mainCtrl.getBoards()));
        }

        boards_view.setCellFactory(new Callback<>() {
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
    }

    private void joinBoard(Long id) {
        try {
            Board board = serverUtils.getBoard(id);
            mainCtrl.showClientOverview(board);
        } catch (WebApplicationException e) {
            invalid_text.setVisible(true);
        }
    }

    /***
     * This is called when a mouse event happens to a board.
     * @param event the mouse event that happened: clicked/clicked twice/...
     */
    public void boardClicked(MouseEvent event) {
        Board board = boards_view.getSelectionModel().getSelectedItem();

        if(mainCtrl.getIsAdmin()){
            leaveBoard.setOpacity(0L);
            leaveBoard.toBack();
        }

        if (board != null) {
            Long boardID = board.getId();

            if (event.getClickCount() == 2)
                joinBoard(boardID);

            buttonBox.setOpacity(1L);
            enterBoard.setOnMouseClicked(eventTwo -> joinBoard(boardID));
            delBoard.setOnMouseClicked(eventTwo -> {

                serverUtils.deleteBoard(boardID);
                buttonBox.setOpacity(0L);
            });

            leaveBoard.setOnMouseClicked(eventTow -> {

                boards_view.getItems().remove(board);
                buttonBox.setOpacity(0L);

                mainCtrl.getBoards().remove(board);

//                try {
//                    saveState();
//                } catch (IOException e) {
//                    System.out.println("State was not saved");
//                }
            });
        }
    }

    /***
     * This is called when the join button is clicked.
     */
    public void joinButtonClicked() {
        try {
            Long id = Long.parseLong(code_input.getText());
            Board board = serverUtils.getBoard(id);
            mainCtrl.addBoard(board);
            mainCtrl.showClientOverview(board);
        } catch (NumberFormatException | WebApplicationException e) {
            invalid_text.setVisible(true);
        }
    }

    public AnchorPane getOverlay() {
        return overlay;
    }

    /**
     * Saves state of the current session. Called when user leaves the main page or exits the app.
     *
     * @throws IOException exception
     */
    public void saveState() throws IOException {
        Path dir = Paths.get("../client/src/main/java/client/sessions_info/");
        System.out.println(dir.getParent());
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file_path));
        String result;
        if (!mainCtrl.getIsAdmin()) {
            result = String.join(" ", mainCtrl.getBoards().stream().
                    map(x -> String.valueOf(x.getId())).toArray(String[]::new));
        } else {
            result = "@";
        }

        writer.write(result);
        writer.close();
    }

    /**
     * Loads state for the current session.
     *
     * @throws FileNotFoundException thrown if no file is found
     */
    public void loadState() throws FileNotFoundException {
        File fileObj = new File(file_path);
        Scanner reader = new Scanner(fileObj);
        if (reader.hasNextLine()) {
            String[] rawData = reader.nextLine().split(" ");

            if (rawData[0].equals("@")) {
                mainCtrl.setIsAdmin(true);
            } else {
                List<Long> ids = new ArrayList<>();
                for (String id : rawData) {
                    ids.add(Long.valueOf(id));
                }
                mainCtrl.setBoards(serverUtils.getBoardsByIds(ids));
            }
        }

        reader.close();
    }
}
