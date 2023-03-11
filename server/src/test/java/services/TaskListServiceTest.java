package services;

import models.Board;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import server.repositories.BoardRepository;
import server.repositories.TaskListRepository;
import server.services.TaskListService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class TaskListServiceTest {
    @InjectMocks
    private TaskListService sut;
    @Mock
    private BoardRepository mockBoardRepo;
    @Mock
    private TaskListRepository mockTaskListRepo;

    @BeforeEach
    public void setup() {
        mockBoardRepo = Mockito.mock(BoardRepository.class);
        mockTaskListRepo = Mockito.mock(TaskListRepository.class);
        sut = new TaskListService(mockTaskListRepo, mockBoardRepo);
    }

    @Test
    void testUpdateExistingTaskList() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);
        TaskList newTaskList = new TaskList("taskList2", board);

        Mockito.when(mockTaskListRepo.findById(10000000L)).thenReturn(Optional.of(taskList));
        Mockito.when(mockTaskListRepo.save(Mockito.any())).thenReturn(taskList);

        TaskList givenTaskList=sut.update(10000000L,newTaskList).getBody();
        assertNotNull(givenTaskList);
        assertEquals(newTaskList.getName(), taskList.getName());
    }

    @Test
    void testUpdateNonExistingTaskList() {
        Board board = new Board("board");
        TaskList newTaskList = new TaskList("taskList2", board);

        Mockito.when(mockTaskListRepo.findById(1000000L)).thenReturn(Optional.empty());
        ResponseEntity<TaskList> response=sut.update(10000000L,newTaskList);

        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddExistingBoard() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);

        Mockito.when(mockBoardRepo.findById(10000000L)).thenReturn(Optional.of(board));
        Mockito.when(mockTaskListRepo.save(Mockito.any())).thenReturn(taskList);

        ResponseEntity<TaskList> response=sut.add(taskList,10000000L);
        assertNotNull(response.getBody());
        assertEquals(taskList.getName(),response.getBody().getName());
    }

    @Test
    void testAddNonExistingBoard() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);

        Mockito.when(mockBoardRepo.findById(10000000L)).thenReturn(Optional.empty());
        ResponseEntity<TaskList> response=sut.add(taskList,10000000L);
        assertNull(response.getBody());
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }
}
