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
import models.Board;
import models.TaskCard;
import models.TaskList;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private String SERVER;

    /**
     * Constructor for server utils with no parameters. Port is set to default 8080
     */
    public ServerUtils() {
        SERVER = "http://localhost:8080/";
    }

    /**
     * Sets the server url
     */
    public void setServer(String server) {
        SERVER = server;
    }

    /**
     * Tests if the connection can be established for a given url
     *
     * @param server server url
     * @return true if connection can be established false otherwise
     */
    public boolean healthCheck(String server) {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/boards") //
                    .request(APPLICATION_JSON) //
                    .accept(APPLICATION_JSON) //
                    .get();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Returns all the boards in the current workspace
     *
     * @return list of boards
     */
    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Returns the board with a given id
     *
     * @param id id of the board
     * @return board
     * @throws WebApplicationException
     */
    public Board getBoard(Long id) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/" + id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Removes the board with a given id
     *
     * @param boardId id of the board
     */
    public void deleteBoard(Long boardId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).delete();
    }

    /**
     * Returns the tasklist with a given id
     *
     * @param id id of the tasklist
     * @return tasklist
     * @throws WebApplicationException
     */
    public TaskList getTaskList(Long id) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasklists/" + id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Returns the taskcard with a given id
     *
     * @param taskId id of the taskcard
     * @return taskcard
     * @throws WebApplicationException
     */
    public TaskCard getTaskCard(Long taskId) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasks/" + taskId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Updates the taskcard with a specified id
     *
     * @param taskId  id of the taskcard
     * @param updated updated taskcard
     */
    public void updateTaskCard(Long taskId, TaskCard updated) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + taskId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(updated));
    }

    /**
     * Updates the board with a specified id
     *
     * @param boardId  id of the board
     * @param updated updated board
     */
    public void updateBoard(Long boardId, Board updated) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(updated));
    }

    public void swapBetweenLists(Long id, int pos, Long idList1, Long idList2) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/swap/" + id + "/" + pos)
                .queryParam("list1", idList1)
                .queryParam("list2", idList2)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(new Board()));
    }

    public void deleteMinimizedCard(Long taskId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + taskId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).delete();
    }

    /**
     * Returns tasklists for a given board
     *
     * @param boardId id of the board
     * @return list of tasklists
     */
    public List<TaskList> getTaskLists(Long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/taskLists/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Returns taskcards for a given tasklist
     *
     * @param taskListId id of the tasklist
     * @return list of taskcards
     */
    public List<TaskCard> getTaskCards(Long taskListId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/taskCards/" + taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Creates a new board
     *
     * @param board new board
     * @return board
     */
    @SuppressWarnings("all")
    public Board addBoard(Board board) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);
    }

    /**
     * Creates a new taskcard in a given tasklist
     *
     * @param card       new taskcard
     * @param taskListId id of the tasklist
     * @return taskcard
     */
    public TaskCard addTaskCard(TaskCard card, Long taskListId) {

        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/").queryParam("taskListId", taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(card, APPLICATION_JSON), TaskCard.class);
    }

    /**
     * Removes tasklist wth a given id
     *
     * @param taskListId id of the tasklist
     * @return removed tasklist
     */
    public TaskList removeTaskList(Long taskListId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/" + taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(TaskList.class);
    }

    /**
     * Returns ids of the tasklists in a specified board
     *
     * @param boardId id of the board
     * @return list of ids
     * @throws WebApplicationException
     */
    public List<Long> getTaskListsId(Long boardId) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId + "/tasklists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Returns ids of the taskcards in a specified tasklist
     *
     * @param listId id of the tasklist
     * @return list of ids
     * @throws WebApplicationException
     */
    public List<Long> getTaskCardsId(Long listId) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/" + listId + "/taskcards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    /**
     * Creates a new tasklist in a given board
     *
     * @param taskList new tasklist
     * @param boardId  id of the board
     * @return tasklist
     */
    public TaskList addTaskList(TaskList taskList, Long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/")
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(taskList, APPLICATION_JSON), TaskList.class);
    }
}
