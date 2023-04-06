package client.utils;

import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.GenericType;
import models.Board;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void testGetBoard() {
        // Set up expected
        Board expectedBoard=new Board("Board1");
        expectedBoard.setId(1L);

        // Set up mocking
        mockServer.when(request().withMethod("GET").withPath("/api/boards/" + expectedBoard.getId()))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedBoard.toString()));

        // Get actual, check
        var actualBoard=sut.getBoard(expectedBoard.getId());
        assertEquals(expectedBoard,actualBoard);
    }

    @Test
    public void testGetBoardsByIds() {
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
        mockServer.when(request().withMethod("POST").withPath("api/boards/boards"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedBoards.toString()));

        // Get actual, check
        List<Board> actualBoards = sut.getBoardsByIds(List.of(1L,2L,3L));
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


}
