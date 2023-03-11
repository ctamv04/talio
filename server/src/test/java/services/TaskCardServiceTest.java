package server.services;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class TaskCardServiceTest {

    @Mock
    private TaskCardRepository taskCardRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskCardService taskCardService;

    private TaskCard taskCard;

    @BeforeEach
    public void setup(){
        taskCardRepository = Mockito.mock(TaskCardRepository.class);
        taskListRepository = Mockito.mock(TaskListRepository.class);
        taskCardService = new TaskCardService(taskCardRepository, taskListRepository);
        taskCard = TaskCard.builder()
            .id(1L)
            .name("Bee Beep")
            .taskList(new TaskList("name", new Board("boardName"))).build();
    }

    @Test
    public void testFindAll(){
        TaskCard taskCard1 = TaskCard.builder()
            .id(1L)
            .name("Boo Boop")
            .taskList(new TaskList("name", new Board("boardName"))).build();

        given(taskCardRepository.findAll()).willReturn(List.of(taskCard,taskCard1));

        List<TaskCard> taskCardList = taskCardService.findAll();

        assertNotNull(taskCardList);
        assertEquals(taskCardList.size(), 2);
    }

    @Test
    public void testFindAllShouldBeNone(){
        TaskCard taskCard1 = TaskCard.builder()
            .id(1L)
            .name("Boo Boop")
            .taskList(new TaskList("name", new Board("boardName"))).build();
        given(taskCardRepository.findAll()).willReturn(Collections.emptyList());
        List<TaskCard> employeeList = taskCardService.findAll();
        assertTrue((employeeList).isEmpty());
        assertEquals(employeeList.size(), 0);
    }

    @Test
    void testCorrectGetById() {
        taskCardRepository.save(taskCard);
        assertNotNull(taskCardService.getById(taskCard.getId()));
    }

    @Test
    void testFalseGetById() {
        taskCardRepository.save(taskCard);
        assertNotNull(taskCardService.getById(11L));
    }

    @Test
    void delete() {
        taskCardRepository.save(taskCard);
        taskCardService.delete(taskCard.getId());
        assertEquals(0, taskCardRepository.findAll().size());
    }

    @Test
    public void testUpdate(){
        taskCardRepository.save(taskCard);
        taskCard.setName("New Name!");
        taskCardService.update(taskCard.getId(), taskCard);
        assertEquals(taskCard.getName(), "New Name!");
    }
}