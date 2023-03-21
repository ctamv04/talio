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
package client.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import models.Board;
import models.TaskCard;
import models.TaskList;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    public Board getBoard(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/"+id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    public boolean existsBoardById(Long id) {
        Response response=ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/"+id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get();
        return response.getStatus()==200;
    }

    public TaskList getTaskList(Long id) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasklists/"+id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }
    
    public TaskCard getTaskCard(Long taskId){
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasks/" + taskId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    public void updateTaskCard(Long taskId, TaskCard updated) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + taskId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(updated));
    }

    public List<TaskList> getTaskLists(Long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/taskLists/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<TaskCard> getTaskCards(Long taskListId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/taskCards/" + taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    @SuppressWarnings("all")
    public Board addBoard(Board board) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board,APPLICATION_JSON), Board.class);
    }

    public TaskCard addTaskCard(TaskCard card, Long taskListId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/").queryParam("taskListId", taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(card, APPLICATION_JSON), TaskCard.class);
    }

    public TaskList removeTaskList(Long taskListId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/" + taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(TaskList.class);
    }
}
