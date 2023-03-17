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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import client.controllers.BoardController;
import client.controllers.ClientOverviewController;
import client.controllers.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

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
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        @Override
        public Object call(Class<?> type) {
            if(type== BoardController.class)
                return new BoardController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class),(Long) params[0]);
            if(type== ClientOverviewController.class)
                return new ClientOverviewController(injector.getInstance(ServerUtils.class),
                        injector.getInstance(MainCtrl.class),(Long)params[0]);
            return injector.getInstance(type);
        }
    }
}