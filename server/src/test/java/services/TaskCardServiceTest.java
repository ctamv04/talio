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

import java.util.Optional;

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
}