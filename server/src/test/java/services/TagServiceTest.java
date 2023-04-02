package services;

import models.Board;
import models.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import server.repositories.BoardRepository;
import server.repositories.TagRepository;
import server.services.TagService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class TagServiceTest {
    @Mock
    private TagRepository mockTagRepository;
    @Mock
    private BoardRepository mockBoardRepository;
    @InjectMocks
    private TagService sut;

    /**
     * Setup before each test
     */
    @BeforeEach
    public void setup() {
        mockTagRepository = Mockito.mock(TagRepository.class);
        mockBoardRepository = Mockito.mock(BoardRepository.class);
        sut = new TagService(mockTagRepository, mockBoardRepository);
    }

    /**
     * Tests update method
     */
    @Test
    public void testUpdate() {
        Tag tag = new Tag();
        tag.setName("test");
        Tag updatedTag = new Tag();
        updatedTag.setName("test2");
        Tag expectedTag = new Tag();
        expectedTag.setName("test2");

        Mockito.when(mockTagRepository.findById(0L)).thenReturn(Optional.of(tag));
        Mockito.when(mockTagRepository.save(Mockito.any())).thenReturn(updatedTag);

        assertEquals(expectedTag.getName(), sut.update(0L, updatedTag).getBody().getName());
    }

    /**
     * Tests add method
     */
    @Test
    public void testAdd() {
        Board board = new Board();
        Tag tag = new Tag();
        Tag expectedTag = new Tag();
        expectedTag.setBoard(board);

        Mockito.when(mockBoardRepository.findById(10000000L)).thenReturn(Optional.of(board));
        Mockito.when(mockBoardRepository.findById(20000000L)).thenReturn(Optional.empty());
        Mockito.when(mockTagRepository.save(Mockito.any())).thenReturn(tag);

        assertEquals(BAD_REQUEST, sut.add(tag, 20000000L).getStatusCode());
        assertEquals(tag, sut.add(tag, 10000000L).getBody());
    }

    /**
     * Tests delete method
     */
    @Test
    public void testDelete() {
        Tag tag = new Tag();
        Board board = new Board();
        tag.setBoard(board);

        Mockito.when(mockTagRepository.findById(0L)).thenReturn(Optional.of(tag));
        Mockito.when(mockTagRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals(BAD_REQUEST, sut.delete(1L).getStatusCode());
        assertEquals(OK, sut.delete(0L).getStatusCode());
    }
}
