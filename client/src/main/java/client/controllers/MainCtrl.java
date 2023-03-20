/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.controllers;

import client.views.ViewFactory;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Board;
import models.TaskList;

public class MainCtrl {

    private Stage primaryStage;
    private Stage secondaryStage;
    private Stage cardStage;

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.secondaryStage = new Stage();
        showStarting();
    }

    public void showStarting() {
        var starting = ViewFactory.createStarting();
        primaryStage.setScene(new Scene(starting.getValue()));
        primaryStage.setTitle("Starting Page");
        primaryStage.show();
    }

    public void showTaskList(TaskList selectedItem) {
        var taskList= ViewFactory.createTaskList(selectedItem.getId());
        primaryStage.setScene(new Scene(taskList.getValue()));
        primaryStage.setTitle("TaskList");
        primaryStage.show();
    }

    public void showStartingMenu() {
        var menu = ViewFactory.createStartingMenu();
        secondaryStage.setScene(new Scene(menu.getValue()));
        secondaryStage.setTitle("Menu");
        secondaryStage.show();
    }

    public void showAddBoardPage() {
        var menu = ViewFactory.createAddBoard();
        secondaryStage.setScene(new Scene(menu.getValue()));
        secondaryStage.setTitle("Add board");
        secondaryStage.show();
    }

    public void showCard(Long card_id) {
        cardStage=new Stage();
        var card= ViewFactory.createCard(card_id);
        cardStage.setScene(new Scene(card.getValue()));
        cardStage.setTitle("Card Details");
        cardStage.initModality(Modality.APPLICATION_MODAL);
        cardStage.showAndWait();
    }

    public void closeCard() {
        if(cardStage!=null)
            cardStage.close();
    }

    public void showClientOverview(Long boardId){
        var clientOverview=ViewFactory.createClientOverview(boardId);
        primaryStage.setScene(new Scene(clientOverview.getValue()));
        primaryStage.setTitle("Client Overview");
        primaryStage.setOnCloseRequest(event -> {
            if(clientOverview.getKey()!=null)
                clientOverview.getKey().closePolling();
        });
        primaryStage.show();
    }
}
