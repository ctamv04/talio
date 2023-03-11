package server.controllers;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;
import server.services.TaskCardService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TaskCardControllerTest {
    private TaskCardService service;
    private TaskCardController controller;
    private TaskCardRepository taskCardRepo;
    private TaskListRepository taskListRepo;

    @BeforeEach
    public void setup() {
        taskCardRepo = new TestTaskCardRepository();
        taskListRepo = new TestTaskListRepository();

        service = new TaskCardService(taskCardRepo, taskListRepo);
        controller = new TaskCardController(repo, service);
    }

    @Test
    public void testConstructor() {
        assertEquals(0, controller.getAll().size());
    }

    @Test
    public void testGetAll() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc = new TaskCard(tl);
        taskListRepo.save(tl);
        controller.add(tc);
        assertEquals(1, controller.getAll().size());
    }

    @Test
    public void testAdd() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc1 = new TaskCard(tl);
        TaskCard tc2 = new TaskCard(tl);

        taskListRepo.save(tl);
        controller.add(tc1);
        assertEquals(1, controller.getAll().size());
        controller.add(tc2);
        assertEquals(2, controller.getAll().size());
        assertEquals(controller.getById(1).getBody(), tc1);
        assertEquals(controller.getById(2).getBody(), tc2);
    }

    @Test
    public void testWrongId() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);
        TaskCard tc = new TaskCard(tl);

        taskListRepo.save(tl);
        controller.add(tc);
        assertEquals(BAD_REQUEST, controller.getById(2).getStatusCode());
    }

    @Test
    public void testRightId() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc = new TaskCard(tl);
        taskListRepo.save(tl);
        controller.add(tc);
        assertEquals(tc, controller.getById(1).getBody());
    }

    @Test
    public void testUpdate() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc = new TaskCard("name1", tl);
        TaskCard newTc = new TaskCard("name2", tl);

        taskListRepo.save(tl);
        controller.add(tc);
        controller.update(1, newTc);
        assertEquals(newTc.getName(), controller.getById(1).getBody().getName());
        assertEquals(newTc.getDescription(), controller.getById(1).getBody().getDescription());
        assertEquals(BAD_REQUEST, controller.update(2, newTc).getStatusCode());
    }

    @Test
    public void testDelete() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc1 = new TaskCard(tl);
        TaskCard tc2 = new TaskCard(tl);

        taskListRepo.save(tl);
        controller.add(tc1);
        controller.add(tc2);
        assertEquals(2, controller.getAll().size());
        controller.delete(1);
        assertEquals(1, controller.getAll().size());
        assertEquals(tc2, controller.getAll().get(0));
    }
}
