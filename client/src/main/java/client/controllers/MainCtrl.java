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
import javafx.stage.Stage;
import models.Board;

public class MainCtrl {

    private Stage primaryStage;
    private Stage secondaryStage;

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        secondaryStage = new Stage();
        showStarting();
    }

    public void showStarting() {
        var starting = ViewFactory.createStarting();
        primaryStage.setScene(new Scene(starting.getValue()));
        primaryStage.setTitle("Starting Page");
        primaryStage.show();
    }


    public void showBoard(Board selectedItem) {
        var board = ViewFactory.createBoard(selectedItem.getId());
        primaryStage.setScene(new Scene(board.getValue()));
        primaryStage.setTitle("Board");
        primaryStage.show();
    }

    public void showMenu() {
        var menu = ViewFactory.createMenu();
        secondaryStage.setScene(new Scene(menu.getValue()));
        secondaryStage.setTitle("Menu");
        secondaryStage.show();
    }

    public void showAddPage() {
        var menu = ViewFactory.createAddBoard();
        secondaryStage.setScene(new Scene(menu.getValue()));
        secondaryStage.setTitle("Add board");
        secondaryStage.show();
    }
}
