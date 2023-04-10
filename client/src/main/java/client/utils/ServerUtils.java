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
import models.Tag;
import models.TaskCard;
import models.TaskList;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import java.util.List;
import java.util.Set;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private String SERVER;
    private String address;

    /**
     * Constructor for server utils with no parameters. Port is set to default 8080
     */
    public ServerUtils() {
        SERVER = "http://localhost:8080/";
        address = "localhost:8080";
    }

    /**
     * Constructor for server utils with a given address
     *
     * @param address address of the server
     */
    @SuppressWarnings("all")
    public void setServer(String address) {
        SERVER = "http://" + address + "/";
        this.address = address;
    }

    /**
     * Get the address of the server
     *
     * @return The address of the server
     */
    public String getAddress() {
        return address;
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
                    .property(ClientProperties.CONNECT_TIMEOUT, 1000)
                    .target(server).path("api/server") //
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
     * @throws WebApplicationException if the id was not found
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
     * Returns boards with specified ids
     *
     * @param ids list of ids
     * @return list of boards
     */
    public List<Board> getBoardsByIds(List<Long> ids) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(ids, APPLICATION_JSON), new GenericType<>() {
                });
    }

    /**
     * Removes the board with a given id
     *
     * @param boardId id of the board
     */
    @SuppressWarnings("all")
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
     * @throws WebApplicationException if the id was not found
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
     * @param taskID id of the taskcard
     * @return taskcard
     * @throws WebApplicationException if the id was not found
     */
    public TaskCard getTaskCard(Long taskID) throws WebApplicationException {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasks/" + taskID) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Updates the TaskCard with a specified ID
     *
     * @param taskID  id of the TaskCard
     * @param updated updated TaskCard
     */
    public void updateTaskCard(Long taskID, TaskCard updated) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + taskID)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(updated));
    }

    /**
     * Updates the board with a specified id
     *
     * @param boardId id of the board
     * @param updated updated board
     */
    public void updateBoard(Long boardId, Board updated) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(updated));
    }

    /**
     * Swap the task card between lists
     *
     * @param id      The id of the card
     * @param pos     The future position
     * @param idList1 The initial list
     * @param idList2 The future list
     */
    public void swapBetweenLists(Long id, int pos, Long idList1, Long idList2) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/swap/" + id + "/" + pos)
                .queryParam("list1", idList1)
                .queryParam("list2", idList2)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(new Board()));
    }

    /**
     * Deletes the TaskCard with a given ID
     *
     * @param taskID ID of the TaskCard
     */
    @SuppressWarnings("all")
    public void deleteMinimizedCard(Long taskID) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + taskID)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).delete();
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
     */
    public void removeTaskList(Long taskListId) {
        ClientBuilder.newClient(new ClientConfig())
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
     * @throws WebApplicationException if the id was not found
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
     * @throws WebApplicationException if the id was not found
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
     */
    public void addTaskList(TaskList taskList, Long boardId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/")
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(taskList, APPLICATION_JSON), TaskList.class);
    }

    /**
     * Register for the long polling for board updates
     *
     * @param id The id of the board
     * @return The response of the long polling
     */
    public Response getBoardUpdates(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/boards/" + id + "/details-updates") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(Response.class);
    }

    /**
     * Register for the long polling for board's tasklists updates
     *
     * @param id The id of the board
     * @return The response of the long polling
     */
    public Response getTaskListIdsUpdates(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasklists/" + id + "/ids-updates") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(Response.class);
    }

    /**
     * Register for the long polling for taskList's task cards updates
     *
     * @param id The id of the taskList
     * @return The response of the long polling
     */
    public Response getTaskCardIdsUpdates(Long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasks/" + id + "/ids-updates") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(Response.class);
    }

    /**
     * Register for the long polling for taskList updates
     *
     * @param taskListId The id of the taskList
     * @return The response of the long polling
     */
    public Response getTaskListUpdates(Long taskListId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tasklists/" + taskListId + "/details-updates") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(Response.class);
    }

    /**
     * Returns current password
     *
     * @return password
     */
    public String getPassword() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/server") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Updates the TaskList with a specified ID
     *
     * @param tasklistID id of the Task list
     * @param updated    updated Task List
     */
    public void updateTaskList(Long tasklistID, TaskList updated) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasklists/" + tasklistID)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).put(Entity.json(updated));
    }

    /**
     * @param cardID The id of the card
     * @return The set of tags
     */
    public Set<Tag> getBoardTags(Long cardID) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/tags/board/" + cardID)
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Updates the tag
     * @param tagID id of the tag
     * @param tag updated tag
     * @return returns the updated tag
     */
    public Tag updateTag(Long tagID, Tag tag) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/tags/" + tagID)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.json(tag), Tag.class);
    }

    /**
     * Adds a new tag to the board
     * @param tag tag to be added
     * @param boardId id of the board
     * @return returns the added tag
     */
    public Tag addTag(Tag tag, Long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tags/")
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    /**
     * Delets tag by id
     *
     * @param tagId tag id
     */
    public void deleteTag(Long tagId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tags/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }
}
