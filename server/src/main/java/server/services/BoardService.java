package server.services;

import models.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import server.repositories.BoardRepository;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public ResponseEntity<Board> update(@PathVariable("id") long id, @RequestBody Board newBoard) {
        return boardRepository.findById(id).map(board -> {
            board.setName(newBoard.getName());
            board.setTaskLists(newBoard.getTaskLists());
            return ResponseEntity.ok(boardRepository.save(board));
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
