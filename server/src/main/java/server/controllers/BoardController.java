package server.controllers;

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

    public BoardController(BoardRepository repo, BoardService service) {
        this.repo = repo;
        this.service = service;
    }

    @GetMapping("")
    public List<Board> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable Long id) {
        Optional<Board> board=repo.findById(id);
        if(board.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(board.get());
    }

    @PostMapping("")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return ResponseEntity.ok(repo.save(board));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable("id") Long id, @RequestBody Board newBoard) {
        return service.update(id,newBoard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable("id") long id) {
        if (!repo.existsById(id))
            return ResponseEntity.badRequest().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
