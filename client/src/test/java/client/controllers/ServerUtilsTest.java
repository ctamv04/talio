package client.controllers;

import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
