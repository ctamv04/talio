package client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import models.Board;
import models.TaskCard;
import models.TaskList;
import models.Tag;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ServerUtilsTest {
    private static final int MOCK_SERVER_PORT = 1234;
    private static ClientAndServer mockServer;
    private final ObjectMapper objectMapper=new ObjectMapper();
    private ServerUtils sut;

    @BeforeAll
    public static void openConnection() {
        mockServer=startClientAndServer(MOCK_SERVER_PORT);
    }

    @AfterAll
    public static void closeConnection() {
        mockServer.stop();
    }

    @BeforeEach
    public void setUp(){
        sut=new ServerUtils();
        sut.setServer("localhost:"+MOCK_SERVER_PORT);
    }

    @Test
    public void testGetAddress(){
        String expectedAddress="localhost:1234";
        assertEquals(expectedAddress,sut.getAddress());
    }

    @Test
    public void testGetBoards() throws JsonProcessingException {
        List<Board> expectedBoards=List.of(
                new Board("Board1"),
                new Board("Board2"),
                new Board("Board3")
        );
        mockServer.when(request().withMethod("GET").withPath("/api/boards"))
                .respond(response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(expectedBoards)));
        List<Board> actualBoards = sut.getBoards();
        assertEquals(expectedBoards,actualBoards);
    }

    @Test
    public void testGetBoard() throws JsonProcessingException {
        // Set up expected
        Board expectedBoard=new Board("Board1");
        expectedBoard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/boards/" + expectedBoard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoard)));

        // Get actual, check
        var actualBoard=sut.getBoard(expectedBoard.getId());
        assertEquals(expectedBoard,actualBoard);
    }

    @Test
    public void testGetBoardsByIds() throws JsonProcessingException {
        // Set up expected
        List<Board> expectedBoards=List.of(
                new Board("Board1"),
                new Board("Board2"),
                new Board("Board3")
        );
        expectedBoards.get(0).setId(1L);
        expectedBoards.get(1).setId(2L);
        expectedBoards.get(2).setId(3L);

        // Set up mocking
        List<Long> ids=List.of(1L,2L,3L);
        mockServer.when(request().withMethod("POST").withPath("/api/boards/boards").withBody(objectMapper.writeValueAsString(ids)))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsBytes(expectedBoards)));

        // Get actual, check
        var actualBoards = sut.getBoardsByIds(List.of(1L,2L,3L));
        assertEquals(expectedBoards,actualBoards);
    }

    @Test
    public void testHealthCheckGood() throws JsonProcessingException {
        // Set up normal mocking
        mockServer.when(request().withMethod("GET").withPath("api/server"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString("OK")));

        // Check that the health check passes with a correct server
        Boolean actualHealthCheck=sut.healthCheck("http://" + sut.getAddress() + "/");
        assertEquals(true,actualHealthCheck);
    }

    @Test
    public void testHealthCheckBad() throws JsonProcessingException {
        // Set up normal mocking
        mockServer.when(request().withMethod("GET").withPath("api/server"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString("OK")));

        // Put some weird value for the server
        Boolean actualHealthCheck=sut.healthCheck("http://" + sut.getAddress() + "4444/");

        // Check that the health check fails
        assertEquals(false,actualHealthCheck);
    }

    @Test
    public void testDeleteBoard() throws JsonProcessingException {
        // Set up expected
        Board expectedBoard=new Board("Board1");
        expectedBoard.setId(1L);

        // Set up mocking
        mockServer.when(request().withPath("/api/boards/" + expectedBoard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoard)));

        // Check that no exception is thrown
        sut.deleteBoard(expectedBoard.getId());
    }

    @Test
    public void testGetTaskList() throws JsonProcessingException {
        // Set up expected
        Board board=new Board("Board1");
        TaskList expectedTaskList=new TaskList();
        expectedTaskList.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/tasklists/" + expectedTaskList.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedTaskList)));

        // Get actual, check
        var actualTaskList=sut.getTaskList(expectedTaskList.getId());
        assertEquals(expectedTaskList,actualTaskList);
    }

    @Test
    public void testGetTaskCard() throws JsonProcessingException {
        // Set up expected
        TaskCard expectedTaskCard=new TaskCard();
        expectedTaskCard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/tasks/" + expectedTaskCard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedTaskCard)));

        // Get actual, check
        var actualTaskCard=sut.getTaskCard(expectedTaskCard.getId());
        assertEquals(expectedTaskCard,actualTaskCard);
    }

    @Test
    public void testUpdateTaskCard() throws JsonProcessingException {
        // Set up expected
        TaskCard expectedTaskCard=new TaskCard();
        TaskCard updatedTaskCard=new TaskCard();
        expectedTaskCard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("PUT").withPath("/api/tasks/" + expectedTaskCard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedTaskCard)));

        // Check that no exception is thrown
        sut.updateTaskCard(expectedTaskCard.getId(), updatedTaskCard);
    }

    @Test
    public void testUpdateBoard() throws JsonProcessingException {
        // Set up expected
        Board expectedBoard=new Board("Board1");
        Board updatedBoard=new Board("Board2");
        expectedBoard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("PUT").withPath("/api/boards/" + expectedBoard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoard)));

        // Check that no exception is thrown
        sut.updateBoard(expectedBoard.getId(), updatedBoard);
    }

    @Test
    public void testSwapBetweenLists() throws JsonProcessingException {
        // Set up expected
        TaskCard taskCard=new TaskCard();
        taskCard.setId(1L);
        TaskList taskList1=new TaskList();
        taskList1.setId(100L);
        TaskList taskList2=new TaskList();
        taskList2.setId(200L);
        int futurePosition = 1;

        // Set up mocking
        mockServer.when(request().withMethod("PUT")
                        .withPath("api/tasks/swap/" + taskCard.getId() + "/" + futurePosition +"/tasklist/")
                        .withQueryStringParameter("list1", String.valueOf(taskList1.getId()))
                        .withQueryStringParameter("list2", String.valueOf(taskList2.getId())))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        // Check that no exception is thrown
        sut.swapBetweenLists(taskCard.getId(), futurePosition, taskList1.getId(), taskList2.getId());
    }

    @Test
    public void testDeleteMinimizedCard() {
        // Set up expected
        TaskCard taskCard=new TaskCard();
        taskCard.setId(1L);

        // Set up mocking
        mockServer.when(request().withPath("/api/tasks/minimized/" + taskCard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));

        // Check that no exception is thrown
        sut.deleteMinimizedCard(taskCard.getId());
    }

    @Test
    public void testAddBoard() throws JsonProcessingException {
        // Set up expected
        Board expectedBoard=new Board("Board1");
        expectedBoard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("POST").withPath("/api/boards"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedBoard)));

        // Get actual, check
        Board actual = sut.addBoard(expectedBoard);
        assertEquals(expectedBoard,actual);
    }

    @Test
    public void testAddTaskCard() throws JsonProcessingException {
        // Set up expected
        TaskCard expectedTaskCard=new TaskCard();
        expectedTaskCard.setId(1L);
        TaskList taskList=new TaskList();
        taskList.setId(100L);

        // Set up mocking
        mockServer.when(request().withMethod("POST").withPath("/api/tasks/")
                        .withQueryStringParameter("taskListId",String.valueOf(taskList.getId()))
                        .withBody(objectMapper.writeValueAsString(expectedTaskCard)))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(objectMapper.writeValueAsString(expectedTaskCard)));

        // Get actual, check
        TaskCard actual = sut.addTaskCard(expectedTaskCard,taskList.getId());
        assertEquals(expectedTaskCard,actual);
    }

    @Test
    public void testRemoveTaskList() throws JsonProcessingException {
        // Set up expected
        TaskList taskList=new TaskList();
        taskList.setId(1L);

        // Set up mocking
        mockServer.when(request().withPath("/api/tasklists/" + taskList.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(taskList)));

        // Check that no exception is thrown
        sut.removeTaskList(taskList.getId());
    }

    @Test
    public void testGetTaskListsId() throws JsonProcessingException {
        // Set up objects
        TaskList taskList=new TaskList();
        TaskList taskList2=new TaskList();
        Board board=new Board("Board1");
        board.setId(1L);
        taskList.setBoard(board);
        taskList2.setBoard(board);
        taskList.setId(10000L);
        taskList2.setId(20000L);

        // Set up expected
        var expected=new ArrayList<Long>() {};
        expected.add(taskList.getId());
        expected.add(taskList2.getId());

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/boards/" + board.getId() + "/tasklists"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expected)));

        // Get actual, check
        var actual = sut.getTaskListsId(board.getId());
        assertEquals(expected,actual);
    }

    @Test
    public void testGetTaskCardsId() throws JsonProcessingException {
        // Set up objects
        TaskCard taskCard=new TaskCard();
        TaskCard taskCard2=new TaskCard();
        TaskList taskList=new TaskList();
        taskList.setId(1L);
        taskCard.setTaskList(taskList);
        taskCard2.setTaskList(taskList);
        taskCard.setId(10000L);
        taskCard2.setId(20000L);

        // Set up expected
        var expected=new ArrayList<Long>() {};
        expected.add(taskCard.getId());
        expected.add(taskCard2.getId());

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/tasklists/" + taskList.getId() + "/taskcards"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expected)));

        // Get actual, check
        var actual = sut.getTaskCardsId(taskList.getId());
        assertEquals(expected,actual);
    }

    @Test
    public void testAddTaskList() throws JsonProcessingException {
        // Set up expected
        TaskList taskList=new TaskList();
        taskList.setId(1L);
        Board board=new Board("Board1");
        board.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("POST").withPath("/api/tasklists/")
                        .withQueryStringParameter("boardId",String.valueOf(board.getId())))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(taskList)));

        // Check that no exception is thrown
        sut.addTaskList(taskList, board.getId());
    }

    @Test
    public void testGetBoardUpdates() throws JsonProcessingException {
        // Set up expected
        Board board=new Board("Board1");
        board.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/boards/" + board.getId() + "/details-updates"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.NO_CONTENT).build())));

        // Get actual, check
        Response actual=sut.getBoardUpdates(board.getId());
        assertNotNull(actual);
    }

    @Test
    public void testGetTaskListIdsUpdates() throws JsonProcessingException {
        // Set up expected
        TaskList taskList = new TaskList();
        taskList.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/tasklists/" + taskList.getId() + "/details-updates"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.NO_CONTENT).build())));

        // Check if it gives a response
        Response actual = sut.getTaskListIdsUpdates(taskList.getId());
        assertNotNull(actual);
    }

    @Test
    public void testGetTaskCardIdsUpdates() throws JsonProcessingException {
        // Set up expected
        TaskCard taskCard = new TaskCard();
        taskCard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/taskcards/" + taskCard.getId() + "/details-updates"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.NO_CONTENT).build())));

        // Check if it gives a response
        Response actual = sut.getTaskCardIdsUpdates(taskCard.getId());
        assertNotNull(actual);
    }

    @Test
    public void testGetTaskListUpdates() throws JsonProcessingException {
        // Set up expected
        TaskList taskList = new TaskList();
        taskList.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/tasklists/" + taskList.getId() + "/details-updates"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.NO_CONTENT).build())));

        // Check if it gives a response
        Response actual = sut.getTaskListUpdates(taskList.getId());
        assertNotNull(actual);
    }

    @Test
    public void testGetPassword() throws JsonProcessingException {
        // Set up expected
        String expected="password";

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/server"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expected));

        // Get actual, check
        String actual = sut.getPassword();
        assertEquals(expected,actual);
    }

    @Test
    public void testUpdateTaskList() throws JsonProcessingException {
        // Set up expected
        TaskList taskList=new TaskList();
        TaskList updatedTaskList=new TaskList();
        taskList.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("PUT").withPath("/api/tasklists/" + taskList.getId()).withBody(objectMapper.writeValueAsString(updatedTaskList)))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(taskList)));

        // Check that no exception is thrown
        sut.updateTaskList(taskList.getId(), updatedTaskList);
    }

    @Test
    public void testGetBoardTags() throws JsonProcessingException {
        // Set up expected
        Tag tag=new Tag();
        tag.setId(1L);
        Tag tag2=new Tag();
        tag2.setId(2L);
        TaskCard taskCard=new TaskCard();
        taskCard.setId(10L);
        var expected=new HashSet<Tag>() {{add(tag); add(tag2);}};
        taskCard.setTags(expected);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/tags/board/" + taskCard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expected)));

        // Get actual, check
        var actual = sut.getBoardTags(taskCard.getId());
        assertEquals(expected,actual);
    }

    @Test
    public void testUpdateTag() throws JsonProcessingException {
        // Set up expected
        Tag tag=new Tag();
        tag.setId(1L);
        Tag updatedTag=new Tag();
        updatedTag.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("PUT").withPath("/api/tags/" + tag.getId()).withBody(objectMapper.writeValueAsString(updatedTag)))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(tag)));

        // Check that no exception is thrown
        sut.updateTag(tag.getId(), updatedTag);
    }

    @Test
    public void testAddTag() throws JsonProcessingException {
        // Set up expected
        Tag tag=new Tag();
        tag.setId(1L);
        Board board=new Board("Board1");
        board.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("POST").withPath("/api/tags/")
                        .withQueryStringParameter("boardId",String.valueOf(board.getId()))
                        .withBody(objectMapper.writeValueAsString(tag)))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(tag)));

        // Get actual, check
        Tag actual=sut.addTag(tag, board.getId());
        assertEquals(tag,actual);
    }

    @Test
    public void testDeleteTag() throws JsonProcessingException {
        // Set up expected
        Tag tag=new Tag();
        tag.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("DELETE").withPath("/api/tags/" + tag.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(tag)));

        // Check that no exception is thrown
        sut.deleteTag(tag.getId());
    }
}
