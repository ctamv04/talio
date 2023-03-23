package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import models.TaskCard;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MinimizedCardController implements Initializable {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final Long taskCardId;
    private Timer timer;
    @FXML
    private Button close_button;
    @FXML
    private Text card_name;

    @Inject
    public MinimizedCardController(ServerUtils serverUtils, MainCtrl mainCtrl, Long taskCardId) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.taskCardId = taskCardId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        },0,500);
    }

    public void update(){
        try{
            TaskCard updatedTaskCard=serverUtils.getTaskCard(taskCardId);

//            System.out.println(updatedTaskCard);

            Platform.runLater(()->{
                card_name.setText(updatedTaskCard.getName());
            });

        }catch (WebApplicationException e){
            closePolling();
        }
    }

    public void closePolling(){
        timer.cancel();
    }

    public void delete() {
        serverUtils.deleteMinimizedCard(this.taskCardId);
    }
}
