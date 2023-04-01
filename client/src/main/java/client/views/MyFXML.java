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
import client.controllers.popups.*;
import client.utils.BoardUtils;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MyFXML {

    private final Injector injector;

    public MyFXML(Injector injector) {
        this.injector = injector;
    }

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

        @Override
        public Object call(Class<?> type) {

            if (type == AddTaskListController.class)
                return new AddTaskListController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Long) params[0]);

            if (type == AddTagController.class)
                return new AddTagController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), injector.getInstance(ExtendedCardUtils.class),
                        (Board) params[0]);

            if (type == BoardController.class)
                return new BoardController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Board) params[0],
                        injector.getInstance(BoardUtils.class));

            if (type == ClientOverviewController.class)
                return new ClientOverviewController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Board) params[0]);

            if (type == ClientMenuController.class)
                return new ClientMenuController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Board) params[0], (BoardController) params[1]);

            if (type == EditBoardController.class)
                return new EditBoardController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Board) params[0]);

            if (type == ExtendedCardController.class)
                return new ExtendedCardController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Long) params[0], injector.getInstance(WebsocketUtils.class), injector.getInstance(ExtendedCardUtils.class));

            if (type == MinimizedCardController.class)
                return new MinimizedCardController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Long) params[0],
                        injector.getInstance(WebsocketUtils.class));

            if (type == TaskListController.class)
                return new TaskListController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class), (Long) params[0], (BoardController) params[1]);

            if (type == BoardDeletedController.class)
                return new BoardDeletedController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class));

            if (type == CardDeletedController.class)
                return new CardDeletedController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class));

            return injector.getInstance(type);
        }
    }
}
