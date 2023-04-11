package client.controllers;

import client.utils.BoardUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import models.Board;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class MainPageController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    private final String file_path;
    private final BoardUtils boardUtils;
    private final BoardController boardController;

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
    private StackPane window;
    @FXML
    private Button admin_login_button;
    @FXML
    private AnchorPane overlay;
    @FXML
    private Text visited_text;

    /***
     * Constructor for LoginController
     * @param serverUtils contains the server and related methods
     * @param mainCtrl the main controller
     * @param boardUtils contains the board and related methods
     * @param boardController the board controller
     */
    @Inject
    public MainPageController(ServerUtils serverUtils, MainCtrl mainCtrl, BoardUtils boardUtils, BoardController boardController) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.boardUtils = boardUtils;
        this.boardController = boardController;
        this.file_path = "../client/src/main/java/client/sessions_info/" + serverUtils.getAddress().replace(':', '_') + ".txt";
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
        configure_state();
        setVisibleBoards();
        startLongPolling(updatedBoards-> Platform.runLater(()-> updateBoards(updatedBoards)));

        overlay.setVisible(false);
        buttonBox.setOpacity(0L);

        invalid_text.setVisible(false);

        boards_view.setOnMouseClicked(this::boardClicked);

        join_board_button.setOnAction(event -> joinButtonClicked());

        new_board_button.setOnAction(event -> {
            overlay.setVisible(true);
            mainCtrl.showAddBoardPage();
            overlay.setVisible(false);
        });

        if (mainCtrl.getIsAdmin()) {
            admin_login_button.setVisible(false);
            visited_text.setText("All Boards");
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
            closePolling();
            mainCtrl.showLoginPage();
        });

        window.setOnMouseClicked(event -> {
            if (event.getTarget() != buttonBox && event.getTarget() != boards_view)
                buttonBox.setOpacity(0);

        });
    }

    /**
     * Configures state
     */
    private void configure_state() {
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
    }

    /**
     * Adds boards to the login page.
     * Admin can see all boards in the current workspace,
     * user can see boards it created or accessed before.
     */
    private void setVisibleBoards() {
        updateBoards(serverUtils.getBoards());

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

    /**
     * Saves the state of the current session
     * @param updatedBoards the list of boards
     */
    public void updateBoards(List<Board> updatedBoards){
        if(mainCtrl.getIsAdmin()){
            Platform.runLater(()->boards_view.setItems(FXCollections.observableArrayList(updatedBoards)));
        }
        else{
            Map<Long,Board> map=new HashMap<>();
            for(var board: updatedBoards)
                map.put(board.getId(),board);
            List<Board> userBoards=mainCtrl.getBoards();
            for(var board: userBoards){
                if(map.get(board.getId())==null)
                    userBoards.remove(board);
                else
                    board.setName(map.get(board.getId()).getName());
            }
            Platform.runLater(()->boards_view.setItems(FXCollections.observableArrayList(userBoards)));
        }
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

        if (mainCtrl.getIsAdmin()) {
            leaveBoard.setOpacity(0L);
            leaveBoard.setDisable(true);
        }

        if (board != null) {
            Long boardID = board.getId();

            if (event.getClickCount() == 2)
                joinBoard(boardID);

            buttonBox.setOpacity(1L);
            enterBoard.setOnMouseClicked(eventTwo -> joinBoard(boardID));
            delBoard.setOnMouseClicked(eventTwo -> {

                boards_view.getItems().remove(board);
                serverUtils.deleteBoard(boardID);
                mainCtrl.getBoards().remove(board);
                buttonBox.setOpacity(0L);
            });

            leaveBoard.setOnMouseClicked(eventTow -> {

                boards_view.getItems().remove(board);
                buttonBox.setOpacity(0L);
                leaveBoard.setDisable(false);


                mainCtrl.getBoards().remove(board);
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
            closePolling();
            mainCtrl.addBoard(board);
            mainCtrl.showClientOverview(board);
        } catch (NumberFormatException | WebApplicationException e) {
            invalid_text.setVisible(true);
        }
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

            List<Long> ids = new ArrayList<>();
            for (String id : rawData) {
                ids.add(Long.valueOf(id));
            }
            mainCtrl.setBoards(serverUtils.getBoardsByIds(ids));
        }

        reader.close();
    }

    /**
     * Saves state of the current session. Called when user leaves the main page or exits the app.
     *
     * @throws IOException exception
     */
    public void saveState() throws IOException {
        if (!mainCtrl.getIsAdmin()) {
            Path dir = Paths.get("../client/src/main/java/client/sessions_info/");
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file_path));
            String result = String.join(" ", mainCtrl.getBoards().stream().
                map(x -> String.valueOf(x.getId())).toArray(String[]::new));

            writer.write(result);
            writer.close();
        }
    }

    private final ExecutorService allBoardsUpdates= Executors.newSingleThreadExecutor();

    /**
     * Start long polling for board details and task list ids.
     * @param consumer the consumer that will be called when a response is received
     */
    public void startLongPolling(Consumer<List<Board>> consumer){
        allBoardsUpdates.submit(()->{
            while(!allBoardsUpdates.isShutdown()){
                var response=serverUtils.getAllBoardsUpdates();
                if(response.getStatus()==204)
                    continue;
                if(response.getStatus()==400){
                    closePolling();
                }
                consumer.accept(response.readEntity(new GenericType<>(){}));
            }
        });
    }

    public void closePolling() {
        allBoardsUpdates.shutdown();
    }
}
