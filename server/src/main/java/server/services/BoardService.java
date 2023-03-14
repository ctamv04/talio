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

    /**
     * Constructor Method
     * @param boardRepository The injected boardRepository of the object
     */
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * Updates a board with a new given one
     * @param id The id of the board that needs updated
     * @param newBoard The new board to be used for update
     * @return Return a response based on the existence of the id
     */
    @Transactional
    public ResponseEntity<Board> update(@PathVariable("id") Long id, @RequestBody Board newBoard) {
        return boardRepository.findById(id).map(board -> {
            board.setName(newBoard.getName());
            return ResponseEntity.ok(boardRepository.save(board));
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
