package server.services;

import models.Board;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.controllers.TestBoardRepository;
import server.controllers.TestTaskListRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class TaskListServiceTest {
    private TaskListService service;
    private TestBoardRepository boardRepo;
    private TestTaskListRepository taskListRepo;

    @BeforeEach
    public void setup() {
        boardRepo = new TestBoardRepository();
        taskListRepo = new TestTaskListRepository();
        service = new TaskListService(taskListRepo, boardRepo);
    }

    @Test
    void update() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);
        TaskList newTaskList = new TaskList("taskList2", board);
        board.getTaskLists().add(taskList);

        boardRepo.save(board);
        service.add(taskList, taskList.getBoard().getId());
        service.update((long) 0, newTaskList);
        assertEquals(newTaskList.getName(), taskListRepo.getById((long) 0).getName());
    }

    @Test
    void testWrongUpdate() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);
        TaskList newTaskList = new TaskList("taskList2", board);
        board.getTaskLists().add(taskList);

        boardRepo.save(board);
        assertEquals(BAD_REQUEST, service.update((long) 0, newTaskList).getStatusCode());
    }

    @Test
    void testAdd() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);
        board.getTaskLists().add(taskList);

        boardRepo.save(board);
        service.add(taskList, taskList.getBoard().getId());
        assertEquals(taskList, taskListRepo.getById((long) 0));
    }

    @Test
    void testWrongAdd() {
        Board board = new Board("board");
        TaskList taskList = new TaskList("taskList1", board);
        board.getTaskLists().add(taskList);

        service.add(taskList, taskList.getBoard().getId());
        assertEquals(BAD_REQUEST, service.add(taskList, taskList.getBoard().getId()).getStatusCode());
    }
}
