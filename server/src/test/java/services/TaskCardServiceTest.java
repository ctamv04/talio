package services;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;
import server.services.TaskCardService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class TaskCardServiceTest {

    @Mock
    private TaskCardRepository mockTaskCardRepository;
    @Mock
    private TaskListRepository mockTaskListRepository;
    @InjectMocks
    private TaskCardService sut;
    private TaskList taskList;
    private TaskCard taskCard;
    private Map<Long, Map<Object, Consumer<List<Long>>>> idsListeners = new ConcurrentHashMap<>();

    @BeforeEach
    public void setup() {
        mockTaskCardRepository = Mockito.mock(TaskCardRepository.class);
        mockTaskListRepository = Mockito.mock(TaskListRepository.class);
        sut=new TaskCardService(mockTaskCardRepository,mockTaskListRepository);
        Board board = new Board("board");
        taskList = new TaskList("taskList", board);
        taskCard = new TaskCard("taskCard1", "Bee Beep1", taskList);
    }

    @Test
    void testUpdateExistingTaskList() {
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.of(taskCard));
        Mockito.when(mockTaskCardRepository.save(Mockito.any())).thenReturn(taskCard);

        TaskCard newTaskCard=new TaskCard("taskCard2","Bee Beep2",taskList);
        TaskCard givenTaskCard=sut.update(10000000L,newTaskCard).getBody();
        assertNotNull(givenTaskCard);
        assertEquals(newTaskCard.getName(), givenTaskCard.getName());
        assertEquals(newTaskCard.getDescription(), givenTaskCard.getDescription());
    }

    @Test
    void testUpdateNonExistingTaskList() {
        TaskCard newTaskCard=new TaskCard("taskCard2","Bee Beep2",taskList);
        Mockito.when(mockTaskCardRepository.findById(1000000L)).thenReturn(Optional.empty());
        ResponseEntity<TaskCard> response=sut.update(10000000L,newTaskCard);

        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddExistingBoard() {
        Mockito.when(mockTaskListRepository.findById(10000000L)).thenReturn(Optional.of(taskList));
        Mockito.when(mockTaskCardRepository.save(Mockito.any())).thenReturn(taskCard);

        ResponseEntity<TaskCard> response=sut.add(taskCard,10000000L);
        assertNotNull(response.getBody());
        assertEquals(taskCard.getName(),response.getBody().getName());
    }

    @Test
    void testAddNonExistingBoard() {
        Mockito.when(mockTaskListRepository.findById(10000000L)).thenReturn(Optional.empty());
        ResponseEntity<TaskCard> response=sut.add(taskCard,10000000L);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteTaskCardBadRequest() {
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.empty());
        ResponseEntity<TaskCard> response=sut.delete(10000000L);
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteExistingTaskCard() {
        TaskCard taskCard2=new TaskCard("taskCard2","Boo Bop",taskList);
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.of(taskCard));
        Mockito.when(mockTaskCardRepository.findById(20000000L)).thenReturn(Optional.of(taskCard2));
        Mockito.when(mockTaskListRepository.getTaskCardsId(taskList.getId())).thenReturn(new ArrayList<>(Arrays.asList(taskCard, taskCard2)));
        ResponseEntity<TaskCard> response=sut.delete(10000000L);
        assertEquals(response, ResponseEntity.ok().build());
    }

    @Test
    void deleteNonExistingTaskCard() {
        // TaskCard not found
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.empty());
        ResponseEntity<TaskCard> response=sut.delete(10000000L);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSwapBetweenListsBadRequest() {
        // TaskCard not found
        TaskList taskList2=new TaskList("taskList2",new Board("board2"));
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.empty());
        Mockito.when(mockTaskListRepository.findById(1L)).thenReturn(Optional.of(taskList2));
        ResponseEntity<TaskCard> response=sut.swapBetweenLists(10000000L,1,1L, 1L, idsListeners);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());

        // Tasklist not found
        TaskCard taskCard2=new TaskCard("taskCard2","Bee Beep2",taskList);
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.of(taskCard2));
        Mockito.when(mockTaskListRepository.findById(1L)).thenReturn(Optional.empty());
        response=sut.swapBetweenLists(10000000L,1,1L, 1L, idsListeners);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());

        // Pos < 0
        Mockito.when(mockTaskListRepository.findById(1L)).thenReturn(Optional.of(taskList2));
        response=sut.swapBetweenLists(10000000L,-1,1L, 1L, idsListeners);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());

        // taskList does not contain taskCard
        response=sut.swapBetweenLists(10000000L,1,1L, 1L, idsListeners);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSwapBetweenDifferentLists() {
        // Set everything up
        TaskList taskList2=new TaskList("taskList2",new Board("board")); // 2L
        TaskCard taskCard2=new TaskCard("taskCard2","Bee Beep2",taskList); // 10000000L
        taskCard.setId(10000000L);
        taskCard2.setId(20000000L);
        taskList.setId(1L);
        taskList2.setId(2L);
        taskList.setTaskCards(new ArrayList<>(Arrays.asList(taskCard2, taskCard, new TaskCard())));
        taskList2.setTaskCards(new ArrayList<>(Arrays.asList(new TaskCard(), new TaskCard())));

        // Mock everything
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.of(taskCard));
        Mockito.when(mockTaskCardRepository.findById(20000000L)).thenReturn(Optional.of(taskCard2));
        Mockito.when(mockTaskListRepository.findById(1L)).thenReturn(Optional.of(taskList));
        Mockito.when(mockTaskListRepository.findById(2L)).thenReturn(Optional.of(taskList2));
        Mockito.when(mockTaskListRepository.getTaskCardsId(taskList.getId())).thenReturn(taskList.getTaskCards());
        Mockito.when(mockTaskListRepository.getTaskCardsId(taskList2.getId())).thenReturn(taskList2.getTaskCards());

        // Test if it works :)
        ResponseEntity<TaskCard> response=sut.swapBetweenLists(10000000L,0,1L, 2L, idsListeners);
        assertEquals(response, ResponseEntity.ok().build());
    }

    @Test
    void testSwapBetweenSameList() {
        // Set everything up
        TaskList taskList1=new TaskList("taskList1",new Board("board")); // 1L
        taskList1.setId(1L);
        taskList1.setTaskCards(new ArrayList<>(Arrays.asList(taskCard, new TaskCard())));

        // Mock everything
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.of(taskCard));
        Mockito.when(mockTaskListRepository.findById(1L)).thenReturn(Optional.of(taskList1));
        Mockito.when(mockTaskCardRepository.save(Mockito.any())).thenReturn(taskCard);
        Mockito.when(mockTaskListRepository.getTaskCardsId(taskList1.getId())).thenReturn(taskList1.getTaskCards());

        // Test if it works for index<pos
        ResponseEntity<TaskCard> response=sut.swapBetweenLists(10000000L,1,1L, 1L, idsListeners);
        assertNotNull(response.getBody());
        assertEquals(taskCard.getName(),response.getBody().getName());

        // Test if it works for index>pos
        taskList1.getTaskCards().set(0, new TaskCard());
        taskList1.getTaskCards().set(1, taskCard);
        response=sut.swapBetweenLists(10000000L,0,1L, 1L, idsListeners);
        assertNotNull(response.getBody());
        assertEquals(taskCard.getName(),response.getBody().getName());
    }

    @Test
    void testTraverseIdsListeners() {
        // Set everything up
        ArrayList<Long> ids=new ArrayList<>();
        Map<Object, Consumer<List<Long>>> listeners=new HashMap<>();
        Consumer<List<Long>> consumer= longs -> {
            for (Long id : longs) {
                ids.add(id);
            }
        };
        listeners.put("test",consumer);
        taskList.setId(1L);
        taskCard.setId(10000000L);
        taskList.setTaskCards(new ArrayList<>(Arrays.asList(taskCard)));

        // Mock everything
        Mockito.when(mockTaskCardRepository.findById(10000000L)).thenReturn(Optional.of(taskCard));
        Mockito.when(mockTaskListRepository.findById(1L)).thenReturn(Optional.of(taskList));
        Mockito.when(mockTaskListRepository.getTaskCardsId(taskList.getId())).thenReturn(taskList.getTaskCards());

        // Test if it works :)
        sut.traverseIdsListeners(listeners,taskList);
        assertEquals(1, ids.size());
        assertTrue(ids.contains(taskCard.getId()));
    }

    @Test
    void testConvertTaskCardsToIds() {
        taskCard.setId(10000000L);
        taskCard.setTaskList(taskList);
        TaskCard taskCard2=new TaskCard("taskCard2","Bee Beep2",taskList);
        taskCard2.setId(10000001L);
        taskList.setTaskCards(new ArrayList<>(Arrays.asList(taskCard,taskCard2)));

        // TaskList empty
        List<Long> actual=sut.convertTaskCardsToIds(null);
        List<Long> expected=null;
        assertEquals(expected,actual);

        // TaskList filled with TaskCards
        expected=new ArrayList<>(Arrays.asList(10000000L, 10000001L));
        actual=sut.convertTaskCardsToIds(taskList.getTaskCards());

        assertEquals(expected, actual);
    }
}