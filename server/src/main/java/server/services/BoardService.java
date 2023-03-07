package server.services;

import models.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import server.repositories.BoardRepository;

import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public ResponseEntity<Board> getById(@PathVariable("id") Long id) {
        if (boardRepository.existsById(id))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(boardRepository.getById(id));
    }
}
