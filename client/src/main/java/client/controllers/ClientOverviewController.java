package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.Board;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientOverviewController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Board board;
    private BoardController boardController;
    private Label boardTitle;
    @FXML
    public BorderPane layout;

    @Inject
    public ClientOverviewController(ServerUtils serverUtils, MainCtrl mainCtrl, Board board) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.board = board;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var boardPair = mainCtrl.createBoard(board);
        boardController = boardPair.getKey();
        var menuPair = mainCtrl.createClientMenu(board, boardController);

        layout.setTop(menuPair.getValue());
        layout.setCenter(boardPair.getValue());

        ClientMenuController clientMenuController=menuPair.getKey();
        boardTitle=clientMenuController.getBoardTitle();
        setBoardTitle();

        clientMenuController.getHome_button().setOnAction(event -> {
            boardController.setActive(false);
            boardController.closePolling();
            mainCtrl.showMainPage();
        });
        boardController.getChangeDetector().addListener(observable -> setBoardTitle());

        Tooltip tooltip = new Tooltip("ID Copied!");
        Button copy_button=clientMenuController.getCopy_button();

        copy_button.setOnMouseEntered(event -> copy_button.setCursor(Cursor.HAND));
        clientMenuController.getCopy_button().setOnMouseClicked(event -> {
            tooltip.show(copy_button,copy_button.localToScene(copy_button.getBoundsInLocal()).getMinX(),
                    copy_button.localToScene(copy_button.getBoundsInLocal()).getMaxY()+ copy_button.getHeight());
            new javafx.animation.Timeline(new javafx.animation.KeyFrame(Duration.seconds(0.5), e -> tooltip.hide())).play();

            Clipboard clipboard=Clipboard.getSystemClipboard();
            ClipboardContent content=new ClipboardContent();
            content.putString(board.getId().toString());
            clipboard.setContent(content);
        });
    }

    public void setBoardTitle(){
        boardTitle.setText("Talio | "+boardController.getBoard().getName()+" (#"+board.getId()+")");
        Text text=new Text(boardTitle.getText());
        System.out.println(text.getLayoutBounds().getWidth()+boardTitle.getPadding().getLeft()+boardTitle.getPadding().getRight());
        boardTitle.setStyle("-fx-text-fill: "+getColor(boardController.getBoard().getFontColor())+";");
    }

    public String getColor(String initialColor){
        if(initialColor.charAt(1)!='x')
            return initialColor;
        return "#"+initialColor.substring(2,8);
    }

    public void closePolling() {
        boardController.closePolling();
    }
}
