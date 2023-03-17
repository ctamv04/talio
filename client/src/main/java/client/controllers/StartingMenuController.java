package client.controllers;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    public ListView<ButtonItem> buttons;

    class ButtonItem {
        public Button button;
        public long id;

        public ButtonItem(long id, Button button) {
            this.id = id;
            this.button = button;
        }

        public String getText() {
            return button.getText();
        }

        public long getId() {
            return id;
        }
    }

    @Inject
    public MenuController(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<ButtonItem> buttonsList = new ArrayList<>();

        Button back_button = new Button("Back");
        back_button.setOnMouseClicked(event -> mainCtrl.showStarting());

        Button add_button = new Button("Add board");
        add_button.setOnMouseClicked(event -> mainCtrl.showAddPage());

        buttonsList.add(new ButtonItem(1, back_button));
        buttonsList.add(new ButtonItem(2, add_button));

        ObservableList<ButtonItem> myObservableList = FXCollections.observableList(buttonsList);
        buttons.setItems(myObservableList);
        buttons.setCellFactory(new Callback<>() {

            public ListCell<ButtonItem> call(ListView<ButtonItem> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ButtonItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        setGraphic(new Label(item.getText()));
                    }
                };
            }
        });
//        System.out.println(buttons.getItems().get(0).get);
        buttons.setOnMouseClicked(event -> handle(buttons.getSelectionModel().getSelectedItem()));
    }

    public void handle(ButtonItem item) {
        if (item.getId() == 1) {
            mainCtrl.showStarting();
        } else {
            mainCtrl.showAddPage();
        }

    }
}
