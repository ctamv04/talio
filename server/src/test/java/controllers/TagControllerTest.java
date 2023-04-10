package controllers;

import models.Board;
import models.Tag;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import server.controllers.TagController;
import server.repositories.TagRepository;
import server.repositories.TaskCardRepository;
import server.services.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TagControllerTest {
    @Mock
    private TaskCardRepository taskCardRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController sut;

    private TaskCard taskCard1;
    private TaskCard taskCard2;
    private TaskCard taskCard3;
    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private List<Tag> tags;

    /**
     * Setup for each test
     */
    @BeforeEach
    public void setup() {
        taskCardRepository = Mockito.mock(TaskCardRepository.class);
        tagRepository = Mockito.mock(TagRepository.class);
        tagService = Mockito.mock(TagService.class);

        sut = new TagController(tagRepository, tagService, taskCardRepository);

        Board board1 = new Board("board1");
        board1.setId(0L);
        TaskList taskList1 = new TaskList("list1", board1);
        taskList1.setId(0L);

        board1.setTaskLists(List.of(taskList1));
        Board board2 = new Board("board1");
        board2.setId(1L);
        TaskList taskList2 = new TaskList("list1", board2);
        taskList2.setId(1L);
        board2.setTaskLists(List.of(taskList2));

        taskCard1 = new TaskCard("Task1", taskList1);
        taskCard2 = new TaskCard("Task2", taskList1);
        taskCard3 = new TaskCard("Task3", taskList2);

        tag1 = new Tag("tag1", board1);
        tag2 = new Tag("tag2", board1);
        tag3 = new Tag("tag3", board2);
        tags = new ArrayList<>() {
            {
                add(tag1);
                add(tag2);
                add(tag3);
            }
        };

        board1.setTags(List.of(tag1, tag2));
        board2.setTags(List.of(tag3));

        taskCard1.setTags(Set.of(tag1, tag2));
        taskCard2.setTags(Set.of(tag3));
    }

    /**
     * Tests getAll method
     */
    @Test
    public void testGetAll() {
        Mockito.when(tagRepository.findAll()).thenReturn(tags);
        assertArrayEquals(tags.toArray(), sut.getAll().toArray());
    }

    /**
     * Tests getById method
     */
    @Test
    public void testGetById() {
        Mockito.when(tagRepository.findById(0L)).thenReturn(Optional.of(tag1));
        Mockito.when(tagRepository.findById(3L)).thenReturn(Optional.empty());

        assertEquals(tag1, sut.getById(0L).getBody());
        assertEquals(BAD_REQUEST, sut.getById(3L).getStatusCode());
    }

    /**
     * Tests getBoardsTags method
     */
    @Test
    public void testGetBoardTags() {
        Mockito.when(taskCardRepository.findById(0L)).thenReturn(Optional.of(taskCard1));
        Mockito.when(taskCardRepository.findById(3L)).thenReturn(Optional.empty());

        assertEquals(List.of(tag1, tag2), sut.getBoardTags(0L).getBody());
        assertEquals(BAD_REQUEST, sut.getBoardTags(3L).getStatusCode());
    }

    /**
     * Tests getTaskTags method
     */
    @Test
    public void testGetTaskTags() {
        Mockito.when(taskCardRepository.findById(1L)).thenReturn(Optional.of(taskCard2));
        Mockito.when(taskCardRepository.findById(3L)).thenReturn(Optional.empty());

        assertEquals(Set.of(tag3), sut.getTaskTags(1L).getBody());
        assertEquals(BAD_REQUEST, sut.getTaskTags(3L).getStatusCode());
    }

    /**
     * Tests add method
     */
    @Test
    public void testAdd() {
        Tag tag4 = new Tag("tag4", taskCard3.getTaskList().getBoard());
        tag4.setTasks(List.of(taskCard3));
        Mockito.when(tagService.add(tag4, tag4.getBoard().getId())).thenReturn(ResponseEntity.of(Optional.of(tag4)));
        Mockito.when(tagService.add(tag4, 3L)).thenReturn(ResponseEntity.of(Optional.empty()));

        assertEquals(tag4, sut.add(tag4, tag4.getBoard().getId()).getBody());
        assertEquals(NOT_FOUND, sut.add(tag4, 3L).getStatusCode());
    }

    /**
     * Tests update method
     */
    @Test
    public void testUpdate() {
        Tag newTag = new Tag("tag4", taskCard2.getTaskList().getBoard());
        newTag.setTasks(List.of(taskCard1));
        Mockito.when(tagService.update(1L, newTag)).thenReturn(ResponseEntity.of(Optional.of(newTag)));
        Mockito.when(tagService.update(3L, newTag)).thenReturn(ResponseEntity.of(Optional.empty()));

        assertEquals(newTag, sut.update(1L, newTag).getBody());
        assertEquals(NOT_FOUND, sut.update(3L, newTag).getStatusCode());
    }

    /**
     * Tests delete method
     */
    @Test
    public void testDelete() {
        Mockito.when(tagService.delete(0L)).thenReturn(ResponseEntity.of(Optional.of(tag1)));
        Mockito.when(tagService.delete(3L)).thenReturn(ResponseEntity.of(Optional.empty()));

        assertEquals(tag1, sut.delete(0L).getBody());
        assertEquals(NOT_FOUND, sut.delete(3L).getStatusCode());
    }
}
