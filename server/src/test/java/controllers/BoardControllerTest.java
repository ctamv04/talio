package controllers;

import models.Board;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import server.controllers.BoardController;
import server.repositories.BoardRepository;
import server.services.BoardService;
import server.services.LongPollingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BoardControllerTest {
    @Mock
    private BoardService boardServiceMock;
    @Mock
    private BoardRepository boardRepositoryMock;
    @Mock
    private LongPollingService longPollingServiceMock;
    @InjectMocks
    private BoardController sut;

    private Board board1;
    private List<Board> boardList;

    @BeforeEach
    public void setup() {
        boardServiceMock = Mockito.mock(BoardService.class);
        boardRepositoryMock = Mockito.mock(BoardRepository.class);
        longPollingServiceMock = Mockito.mock(LongPollingService.class);
        sut = new BoardController(boardRepositoryMock, boardServiceMock, longPollingServiceMock);

        board1 = new Board("board1");
        board1.setId(1L);
        Board board2 = new Board("board2");
        board2.setId(2L);
        boardList = new ArrayList<>(List.of(board1, board2));
        TaskList taskList1 = new TaskList("list1", board1);
        TaskList taskList2 = new TaskList("list1", board1);
        taskList1.setId(1L);
        taskList2.setId(2L);
        board1.setTaskLists(List.of(taskList1, taskList2));
    }

    @Test
    public void testGetAll() {
        Mockito.when(boardRepositoryMock.findAll()).thenReturn(boardList);
        assertEquals(boardList, sut.getAll());
    }

    @Test
    public void testGetById() {
        Mockito.when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(board1));
        Mockito.when(boardRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertEquals(board1, sut.getById(1L).getBody());
        assertEquals(400, sut.getById(3L).getStatusCodeValue());
    }

    @Test
    public void testGetByIds() {
        Board board2 = new Board();
        Mockito.when(boardRepositoryMock.findAllById(new ArrayList<>() {{
            add(1L);
            add(2L);
        }})).thenReturn(List.of(board1, board2));
        Mockito.when(boardRepositoryMock.findAllById(new ArrayList<>())).thenReturn(List.of());

        assertEquals(List.of(board1, board2), sut.getByIds(new ArrayList<>() {{
            add(1L);
            add(2L);
        }}).getBody());
        assertEquals(0, sut.getByIds(new ArrayList<>()).getBody().size());
    }

    @Test
    public void testGetTaskListsId() {
        Mockito.when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(board1));
        Mockito.when(boardRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertEquals(List.of(1L, 2L), sut.getTaskListsId(1L).getBody());
        assertEquals(BAD_REQUEST, sut.getTaskListsId(3L).getStatusCode());
    }

    @Test
    public void testGetTaskLists() {
        Mockito.when(boardRepositoryMock.findById(1L)).thenReturn(Optional.of(board1));
        Mockito.when(boardRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertEquals(board1.getTaskLists(), sut.getTaskLists(1L).getBody());
        assertEquals(BAD_REQUEST, sut.getTaskListsId(3L).getStatusCode());
    }

    @Test
    public void testAdd() {
        Board board3 = new Board("Board3");
        Mockito.when(boardRepositoryMock.save(board3)).thenReturn(board3);

        assertEquals(board3, sut.add(board3).getBody());
    }

    @Test
    public void testUpdate() {
        Board newBoard = new Board("newBoard");
        newBoard.setId(1L);
        Mockito.when(boardServiceMock.update(1L, newBoard)).thenReturn(ResponseEntity.ok(board1));
        Mockito.when(boardServiceMock.update(3L, newBoard)).thenReturn(ResponseEntity.badRequest().build());

        assertNotNull(sut.update(1L, newBoard).getBody());
        assertEquals(400, sut.update(3L, newBoard).getStatusCodeValue());
    }

    @Test
    public void testDelete() {
        Mockito.when(boardRepositoryMock.existsById(1L)).thenReturn(true);
        Mockito.when(boardRepositoryMock.existsById(3L)).thenReturn(false);

        assertEquals(ResponseEntity.ok().build(), sut.delete(1L));
        assertEquals(BAD_REQUEST, sut.delete(3L).getStatusCode());
    }

    @Test
    public void testGetDetailsUpdate() {
        Mockito.when(longPollingServiceMock.getUpdates(1L, new HashMap<>())).thenReturn(new DeferredResult<>());
        assertNotNull(sut.getDetailsUpdates(1L));
    }
}
