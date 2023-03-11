package server.controllers;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.BoardService;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class BoardControllerTest {
    private BoardService service;
    private BoardController controller;
    private TestBoardRepository repo;

    @BeforeEach
    public void setup() {
        repo = new TestBoardRepository();
        service = new BoardService(repo);
        controller = new BoardController(repo, service);
    }

    @Test
    public void testGetAll() {
        List<Board> boardsList = new ArrayList<>();

        Board board1 = new Board("board1");
        Board board2 = new Board("board2");
        TaskList taskList = new TaskList("taskList1", board1);
        board1.setTaskLists(List.of(taskList));

        repo.save(board1);
        repo.save(board2);

        boardsList.add(board1);
        boardsList.add(board2);

        assertEquals(boardsList, controller.getAll());
    }

    @Test
    public void testGetById() {
        List<Board> boardsList = new ArrayList<>();

        Board board1 = new Board("board1");
        Board board2 = new Board("board2");
        TaskList taskList = new TaskList("taskList1", board1);
        board1.setTaskLists(List.of(taskList));

        repo.save(board1);
        repo.save(board2);

        boardsList.add(board1);
        boardsList.add(board2);

        assertEquals(boardsList.get(0), controller.getById((long) 0).getBody());
    }

    @Test
    public void testGetByIdWrongId() {
        assertEquals(BAD_REQUEST, controller.getById((long) 2023).getStatusCode());
    }

    @Test
    public void testAdd() {
        Board board1 = new Board("board1");
        Board board2 = new Board("board2");
        TaskList taskList = new TaskList("taskList1", board1);
        board1.setTaskLists(List.of(taskList));

        controller.add(board1);
        controller.add(board2);

        assertEquals(List.of(board1, board2), repo.findAll());
    }

    @Test
    public void testUpdate() {
        Board board1 = new Board("board1");
        Board board2 = new Board("board2");
        TaskList taskList = new TaskList("taskList1", board1);
        board1.setTaskLists(List.of(taskList));
        board2.setId((long) 1);

        repo.save(board1);

        controller.update((long) 0, board2);

        assertEquals(board2, repo.findAll().get(0));
    }

    @Test
    public void testDelete() {
        Board board1 = new Board("board1");
        Board board2 = new Board("board2");
        TaskList taskList = new TaskList("taskList1", board1);
        board1.setTaskLists(List.of(taskList));
        board2.setId((long) 1);

        repo.save(board1);
        repo.save(board2);

        controller.delete((long) 1);

        assertEquals(List.of(board1), repo.findAll());
    }
}
