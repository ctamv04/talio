package services;

import models.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import server.repositories.BoardRepository;
import server.services.BoardService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BoardServiceTest {

    @Mock
    private BoardRepository mockBoardRepository;
    @InjectMocks
    private BoardService sut;

    @BeforeEach
    public void setup(){
        mockBoardRepository = Mockito.mock(BoardRepository.class);
        sut = new BoardService(mockBoardRepository);
    }

    @Test
    public void testUpdateExistingBoard(){
        Board board = new Board("Dummy Board");
        Board expectedBoard= new Board("New Board");
        Mockito.when(mockBoardRepository.findById(10000000L)).thenReturn(Optional.of(board));
        Mockito.when(mockBoardRepository.save(Mockito.any())).thenReturn(expectedBoard);
        Board givenBoard=sut.update(10000000L,new Board("New Board")).getBody();

        Mockito.verify(mockBoardRepository).findById(10000000L);
        assertNotNull(givenBoard);
        assertEquals(expectedBoard.getName(),givenBoard.getName());
    }
    @Test
    public void testUpdateNonExistingBoard(){
        Mockito.when(mockBoardRepository.findById(10000000L)).thenReturn(Optional.empty());
        Board givenBoard=sut.update(10000000L,new Board("New Board")).getBody();

        Mockito.verify(mockBoardRepository).findById(10000000L);
        assertNull(givenBoard);
    }

}