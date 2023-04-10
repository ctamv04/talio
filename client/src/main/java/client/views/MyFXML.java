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
package client.views;

import client.controllers.*;
import client.controllers.popups.AddTaskListController;
import client.controllers.popups.BoardDeletedController;
import client.controllers.popups.EditBoardController;
import client.controllers.popups.UpdateTagController;
import client.utils.BoardUtils;
import client.utils.ExtendedCardUtils;
import client.utils.ServerUtils;
import client.utils.WebsocketUtils;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;
import models.Board;
import models.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MyFXML {

    private final Injector injector;

    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    /**
     * Creates new scene
     *
     * @param c      controller class
     * @param url    path to fxml file
     * @param params params for a constructor of a given controller
     * @param <T>    type of controller
     * @return pair of controller and Parent object
     */
    @SuppressWarnings("unused")
    public <T> Pair<T, Parent> load(Class<T> c, String url, Object... params) {
        try {
            var loader = new FXMLLoader(getClass().getResource(url),
                    null,
                    null,
                    new MyFactory(params),
                    StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {
        private final Object[] params;

        public MyFactory(Object[] params) {
            this.params = params;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return (Builder) () -> injector.getInstance(type);
        }

        /**
         * Creates new controller object of a required type
         *
         * @param type class of controller
         * @return new controller
         */
        @Override
        public Object call(Class<?> type) {

            switch (type.getSimpleName()) {
                case "AddTaskListController":
                    return new AddTaskListController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Long) params[0]);
                case "UpdateTagController":
                    return new UpdateTagController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), injector.getInstance(ExtendedCardUtils.class),
                            (EditBoardController) params[0], (Tag) params[1]);
                case "BoardController":
                    return new BoardController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Board) params[0],
                            injector.getInstance(BoardUtils.class));
                case "ClientOverviewController":
                    return new ClientOverviewController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Board) params[0]);
                case "ClientMenuController":
                    return new ClientMenuController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Board) params[0], (BoardController) params[1]);
                case "EditBoardController":
                    return new EditBoardController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Board) params[0]);
                case "ExtendedCardController":
                    return new ExtendedCardController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Long) params[0], injector.getInstance(WebsocketUtils.class),
                            injector.getInstance(ExtendedCardUtils.class));
                case "MinimizedCardController":
                    return new MinimizedCardController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Long) params[0],
                            injector.getInstance(WebsocketUtils.class), (BoardController) params[1],
                            (TaskListController) params[2]);
                case "TaskListController":
                    return new TaskListController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class), (Long) params[0], (BoardController) params[1]);
                case "BoardDeletedController":
                    return new BoardDeletedController(injector.getInstance(ServerUtils.class),
                            injector.getInstance(MainCtrl.class));
                default:
                    return injector.getInstance(type);
            }

        }
    }
}
