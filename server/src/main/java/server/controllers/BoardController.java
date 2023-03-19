package server.controllers;

import models.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import models.Board;
import server.repositories.BoardRepository;
import server.services.BoardService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository repo;
    private final BoardService service;

    /**
     * Constructor Method
     * @param repo The injected repository of the object
     * @param service The injected service of the object
     */
    public BoardController(BoardRepository repo, BoardService service) {
        this.repo = repo;
        this.service = service;
    }

    /**
     * Fetch all Method
     * @return All the existing boards
     */
    @GetMapping("")
    public List<Board> getAll() {
        return repo.findAll();
    }

    /**
     * Find by id method
     * @param id The id of the wanted board
     * @return A response based on the existence of the board
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable Long id) {
        Optional<Board> board=repo.findById(id);
        return board.map(ResponseEntity::ok).
                        orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Get task lists of the board
     * @param id The id of the board containing the task lists
     * @return A list of the task lists belonging to the board
     */
    @GetMapping("/taskLists/{id}")
    public ResponseEntity<List<TaskList>> getTaskLists(@PathVariable Long id) {
        Optional<Board> board = repo.findById(id);
        return board.map(value -> ResponseEntity.ok(value.getTaskLists())).
                        orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a new board method
     * @param board The new board
     * @return A positive response
     */
    @PostMapping("")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return ResponseEntity.ok(repo.save(board));
    }

    /**
     * Update an existing board
     * @param id The id of the current board
     * @param newBoard The new board
     * @return A response based on the existence of the board
     */
    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable("id") Long id, @RequestBody Board newBoard) {
        return service.update(id,newBoard);
    }

    /**
     * Delete an existing board
     * @param id The id of the current board
     * @return A response based on the existence of the board
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable("id") Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.badRequest().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
