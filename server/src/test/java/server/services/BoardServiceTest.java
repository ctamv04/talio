package server.services;

import models.Board;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import server.repositories.BoardRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    private Board board;

    @BeforeEach
    public void setup(){
        boardRepository = Mockito.mock(BoardRepository.class);
        boardService = new BoardService(boardRepository);
        board = Board.builder()
            .id(1L)
            .name("Bee Beep")
            .taskLists(new ArrayList<TaskList>()).build();
    }

    @Test
    public void update(){
        boardRepository.save(board);
        board.setName("Talio");
        Long boardId = board.getId();

        boardService.update(boardId, board).getBody();

        assertEquals(board.getName(),"Talio");
        assertEquals(board.getId(), boardId);
    }
}