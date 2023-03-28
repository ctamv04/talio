package controllers;

import mocks.TestTaskCardRepository;
import mocks.TestTaskListRepository;
import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.controllers.TaskCardController;
import server.services.TaskCardService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TaskCardControllerTest {
    private TaskCardController controller;
    private TestTaskListRepository taskListRepo;

    @BeforeEach
    public void setup() {
        TestTaskCardRepository taskCardRepo = new TestTaskCardRepository();
        taskListRepo = new TestTaskListRepository();

        TaskCardService service = new TaskCardService(taskCardRepo, taskListRepo);
        controller = new TaskCardController(taskCardRepo, service, null);
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
        controller.add(tc,0L);
        assertEquals(1, controller.getAll().size());
    }

    @Test
    public void testAdd() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc1 = new TaskCard(tl);
        TaskCard tc2 = new TaskCard(tl);

        taskListRepo.save(tl);
        controller.add(tc1,0L);
        assertEquals(1, controller.getAll().size());
        controller.add(tc2,0L);
        assertEquals(2, controller.getAll().size());
        assertEquals(controller.getById(1L).getBody(), tc1);
        assertEquals(controller.getById(2L).getBody(), tc2);
    }

    @Test
    public void testWrongId() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);
        TaskCard tc = new TaskCard(tl);

        taskListRepo.save(tl);
        controller.add(tc,0L);
        assertEquals(BAD_REQUEST, controller.getById(2L).getStatusCode());
    }

    @Test
    public void testRightId() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc = new TaskCard(tl);
        taskListRepo.save(tl);
        controller.add(tc,0L);
        assertEquals(tc, controller.getById(1L).getBody());
    }

    @Test
    public void testUpdate() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc = new TaskCard("name1", tl);
        TaskCard newTc = new TaskCard("name2", tl);

        taskListRepo.save(tl);
        controller.add(tc,0L);
        controller.update(1L, newTc);
        ResponseEntity<TaskCard> response=controller.getById(1L);
        assertNotNull(response.getBody());
        assertEquals(newTc.getName(), response.getBody().getName());
        assertEquals(newTc.getDescription(), response.getBody().getDescription());
        assertEquals(BAD_REQUEST, controller.update(2L, newTc).getStatusCode());
    }

    @Test
    public void testDelete() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        TaskCard tc1 = new TaskCard(tl);
        TaskCard tc2 = new TaskCard(tl);

        taskListRepo.save(tl);
        controller.add(tc1,0L);
        controller.add(tc2,0L);
        assertEquals(2, controller.getAll().size());
    }

    @Test
    public void testDeleteFalse() {
        Board b = new Board("board1");
        TaskList tl = new TaskList("taskList1", b);

        taskListRepo.save(tl);

        ResponseEntity<TaskCard> response=controller.delete(1L);
        assertEquals(BAD_REQUEST,response.getStatusCode());
    }
}
