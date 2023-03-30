package controllers;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import server.controllers.TaskCardController;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;
import server.services.LongPollingService;
import server.services.TaskCardService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TaskCardControllerTest {
    @Mock
    private TaskListRepository taskListRepositoryMock;
    @Mock
    private LongPollingService longPollingServiceMock;
    @Mock
    private TaskCardService taskCardServiceMock;
    @Mock
    private TaskCardRepository taskCardRepositoryMock;
    @Mock
    private SimpMessagingTemplate messagesMock;
    @InjectMocks
    private TaskCardController sut;
    private TaskCard taskCard1;
    private TaskList taskList1;
    private TaskList taskList2;
    private List<TaskCard> taskCardList;
    private final Map<Long, Map<Object, Consumer<List<Long>>>> idsListeners = new ConcurrentHashMap<>();


    /**
     * Setup for each test
     */
    @BeforeEach
        public void setup() {
            taskCardRepositoryMock = Mockito.mock(TaskCardRepository.class);
            taskCardServiceMock = Mockito.mock(TaskCardService.class);
            longPollingServiceMock = Mockito.mock(LongPollingService.class);
            taskListRepositoryMock = Mockito.mock(TaskListRepository.class);
            messagesMock = Mockito.mock(SimpMessagingTemplate.class);

            sut = new TaskCardController(taskCardRepositoryMock, taskCardServiceMock, longPollingServiceMock, taskListRepositoryMock, messagesMock);

            Board board1 = new Board("board1");
            board1.setId(1L);
            taskList1 = new TaskList("list1", board1);
            taskList1.setId(1L);
            taskList2 = new TaskList("list2", board1);
            taskList2.setId(2L);
            board1.setTaskLists(List.of(taskList1, taskList2));
            taskCard1 = new TaskCard("Task1", taskList1);
        TaskCard taskCard2 = new TaskCard("Task2", taskList1);
            taskCard1.setId(1L);
            taskCard2.setId(2L);
            taskCardList = new ArrayList<>(List.of(taskCard1, taskCard2));
            taskList1.setTaskCards(taskCardList);
        }

    /**
     * Tests the getAll function
     */
    @Test
    public void testGetAll() {
        Mockito.when(taskCardRepositoryMock.findAll()).thenReturn(taskCardList);
        assertEquals(taskCardList, sut.getAll());
    }

    /**
     * Tests the getById function
     */
    @Test
    public void testGetById() {
        Mockito.when(taskCardRepositoryMock.findById(1L)).thenReturn(Optional.of(taskCard1));
        Mockito.when(taskCardRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertEquals(taskCard1,sut.getById(1L).getBody());
        assertEquals(400,sut.getById(3L).getStatusCodeValue());
    }

    /**
     * Tests the getBoardId function
     */
    @Test
    public void testGetBoardId() {
        Mockito.when(taskCardRepositoryMock.findById(1L)).thenReturn(Optional.of(taskCard1));
        Mockito.when(taskCardRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertEquals(1L,sut.getBoardId(1L).getBody());
        assertEquals(BAD_REQUEST,sut.getBoardId(2L).getStatusCode());
    }

    /**
     * Tests the add function
     */
    @Test
    public void testAdd() {
        TaskCard taskCard3 = new TaskCard("Task Card 3", taskList1);
        taskCardList.add(taskCard3);
        taskList1.setTaskCards(taskCardList);
        Mockito.when(taskCardServiceMock.add(taskCard3, taskCard3.getTaskList().getId())).thenReturn(ResponseEntity.of(Optional.of(taskCard3)));

        assertEquals(taskCard3,sut.add(taskCard3, taskCard3.getTaskList().getId()).getBody());
    }

    /**
     * Tests the update function
     */
    @Test
    public void testUpdate() {
        TaskCard newTaskCard = new TaskCard("New", taskList1);
        newTaskCard.setId(1L);
        Mockito.when(taskCardServiceMock.update(1L,newTaskCard)).thenReturn(ResponseEntity.ok(taskCard1));
        Mockito.when(taskCardServiceMock.update(3L,newTaskCard)).thenReturn(ResponseEntity.badRequest().build());

        assertNotNull(sut.update(1L,newTaskCard).getBody());
        assertEquals(400,sut.update(3L,newTaskCard).getStatusCodeValue());
    }

    /**
     * Tests the swapBetweenLists function
     */
    @Test
    public void testSwapBetweenLists() {
        Mockito.when(taskCardRepositoryMock.findById(1L)).thenReturn(Optional.of(taskCard1));
        Mockito.when(taskListRepositoryMock.findById(1L)).thenReturn(Optional.of(taskList1));
        Mockito.when(taskListRepositoryMock.findById(2L)).thenReturn(Optional.of(taskList2));
        Mockito.when(taskListRepositoryMock.save(taskList1)).thenReturn(taskList1);
        Mockito.when(taskListRepositoryMock.save(taskList2)).thenReturn(taskList2);
        Mockito.when(taskCardRepositoryMock.save(taskCard1)).thenReturn(taskCard1);
        Mockito.when(taskCardServiceMock.swapBetweenLists(1L, taskCard1.getPosition(), 1L, 2L, idsListeners)).thenReturn(ResponseEntity.of(Optional.of(taskCard1)));
        assertEquals(ResponseEntity.ok(taskCard1), sut.swapBetweenLists(1L, taskCard1.getPosition(), 1L, 2L));
    }

    /**
     * Tests the delete function
     */
    @Test
    public void testDelete() {
        Mockito.when(taskCardRepositoryMock.findById(1L)).thenReturn(Optional.of(taskCard1));
        Mockito.when(taskCardServiceMock.delete(1L)).thenReturn(ResponseEntity.of(Optional.of(taskCard1)));
        Mockito.when(taskCardRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.ok(taskCard1), sut.delete(1L));
        assertEquals(BAD_REQUEST,sut.delete(3L).getStatusCode());
    }

    /**
     * Tests the getIdsUpdates function
     */
    @Test
    public void testGetIdsUpdates() {
        Mockito.when(longPollingServiceMock.getUpdates(1L,idsListeners)).thenReturn(new DeferredResult<>());
        assertNotNull(sut.getIdsUpdates(1L));
    }
}
