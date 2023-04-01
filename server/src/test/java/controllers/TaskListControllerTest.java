package controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import server.controllers.TaskListController;
import server.repositories.BoardRepository;
import server.repositories.TaskListRepository;
import server.services.LongPollingService;
import server.services.TaskListService;

public class TaskListControllerTest {

    @Mock
    private TaskListRepository listRepo;
    @Mock
    private BoardRepository boardRepo;
    @Mock
    private LongPollingService longPollingMock;
    @Mock
    private TaskListService service;
    @InjectMocks
    private TaskListController sut;

    Board board;
    TaskList list1;
    List<TaskList> lists;
    TaskList list3;
    TaskCard card1;
    TaskCard card2;

    /**
     * Setup method for the test methods
     */
    @BeforeEach
    public void setup() {

        listRepo = Mockito.mock(TaskListRepository.class);
        boardRepo = Mockito.mock(BoardRepository.class);
        service = new TaskListService(listRepo, boardRepo);
        longPollingMock = Mockito.mock(LongPollingService.class);
        sut = new TaskListController(listRepo, service, longPollingMock);

        board = new Board();
        board.setId(1L);

        list1 = new TaskList("1", board);
        list1.setId(1L);
        list3 = new TaskList("2", board);
        list3.setId(3L);

        lists = new ArrayList<>();
        lists.add(list1);
        lists.add(list3);
        board.setTaskLists(lists);

        card1 = new TaskCard("1", list1);
        card1.setId(1L);
        card2 = new TaskCard("2", list1);
        card2.setId(2L);

        list1.setTaskCards(List.of(card1, card2));

        Mockito.when(boardRepo.findById(1L)).thenReturn(Optional.of(board));
        Mockito.when(listRepo.save(list1)).thenReturn(list1);
        Mockito.when(listRepo.save(list3)).thenReturn(list3);
        Mockito.when(listRepo.findById(1L)).thenReturn(Optional.of(list1));
        Mockito.when(listRepo.getTaskCardsId(list1.getId())).thenReturn(List.of(card1, card2));
        Mockito.when(listRepo.findAll()).thenReturn(lists);
        Mockito.when(listRepo.getById(list1.getId())).thenReturn(list1);


    }

    /**
     * Success case for TaskList addition
     */
    @Test
    public void addTaskListFailure() {

        assertEquals(HttpStatus.BAD_REQUEST, sut.add(list1, 2L).getStatusCode());
    }

    /**
     * Success case for TaskList addition
     */
    @Test
    public void addTaskListSuccessCode() {

        TaskList list2 = new TaskList("3", board);
        Mockito.when(listRepo.save(list2)).thenReturn(list2);

        assertEquals(HttpStatus.OK, sut.add(list2, 1L).getStatusCode());
    }

    /**
     * Success case for TaskList addition
     */
    @Test
    public void addTaskListSuccessBody() {

        TaskList list2 = new TaskList("3", board);
        Mockito.when(listRepo.save(list2)).thenReturn(list2);

        assertEquals(list2, sut.add(list2, 1L).getBody());
    }

    /**
     * Success case for TaskCard retrieval
     */
    @Test
    public void getTaskCardTestSuccess() {

        assertEquals(list1, sut.getById(1L).getBody());
        assertEquals(HttpStatus.OK, sut.getById(1L).getStatusCode());
    }

    /**
     * Success case for TaskCard retrieval
     */
    @Test
    public void getAllSuccess() {

        assertEquals(lists, sut.getAll());
    }

    /**
     * Fail case for TaskCard retrieval
     */
    @Test
    public void getAllFailure() {

        assertNotEquals(List.of(list1), sut.getAll());
    }

    /**
     * Success case for TaskCard ID retrieval
     */
    @Test
    public void getTaskCardsIdSuccess() {

        assertEquals(List.of(card1.getId(), card2.getId()), sut.getTaskCardsId(list1.getId()).getBody());
    }

    /**
     * Fail case for TaskCard ID retrieval
     */
    @Test
    public void getTaskCardsIdFailure() {

        assertEquals(HttpStatus.BAD_REQUEST, sut.getTaskCardsId(2L).getStatusCode());
    }

    /**
     * Success case for TaskCard retrieval
     */
    @Test
    public void getTaskCardsSuccess() {

        assertEquals(List.of(card1, card2), sut.getTaskCard(list1.getId()).getBody());
    }

    /**
     * Fail case for TaskCard retrieval
     */
    @Test
    public void getTaskCardsFailure() {

        assertEquals(HttpStatus.BAD_REQUEST, sut.getTaskCard(2L).getStatusCode());
        assertNull(sut.getTaskCard(2L).getBody());
    }

    /**
     * Success case for TaskList update
     */
    @Test
    public void updateSuccess() {

        assertEquals(HttpStatus.OK, sut.update(list1.getId(), list3).getStatusCode());
        assertEquals(listRepo.getById(1L).getName(), list3.getName());
    }

    /**
     * Fail case for TaskList update
     */
    @Test
    public void updateFailure() {

        assertEquals(HttpStatus.BAD_REQUEST, sut.update(4L, list3).getStatusCode());
        assertNull(sut.update(4L, list3).getBody());
    }

    /**
     * Success case for TaskList delete
     */
    @Test
    public void deleteSuccess() {

        Mockito.when(listRepo.existsById(list1.getId())).thenReturn(true);
        assertEquals(HttpStatus.OK, sut.delete(1L).getStatusCode());
    }

    /**
     * Fail case for TaskList delete
     */
    @Test
    public void deleteFailure() {

        Mockito.when(listRepo.existsById(list1.getId())).thenReturn(false);
        assertEquals(HttpStatus.BAD_REQUEST, sut.delete(1L).getStatusCode());
    }

}