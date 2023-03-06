package server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import models.Board;
import server.repositories.BoardRepository;
import server.services.IsNullOrEmptyService;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardRepository repo;

    public BoardController(BoardRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = { "", "/" })
    public List<Board> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.getById(id));
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return ResponseEntity.ok(repo.save(board));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable("id") long id, @RequestBody Board board) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)) {
            add(board);
        }

        return ResponseEntity.ok(board);

        //TODO: update functionality after board model is done
    }

    @SuppressWarnings("rawtypes")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
