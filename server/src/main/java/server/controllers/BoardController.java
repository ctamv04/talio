package server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import models.Board;
import server.repositories.BoardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository repo;

    public BoardController(BoardRepository repo) {
        this.repo = repo;
    }

    @GetMapping("")
    public List<Board> getAll() {
        return repo.findAll();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return ResponseEntity.ok(repo.save(board));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable("id") long id, @RequestBody Board newBoard) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return repo.findById(id).map(board -> {
            board.setName(newBoard.getName());
            board.setTaskLists(newBoard.getTaskLists());
            return ResponseEntity.ok(repo.save(board));
        }).orElseGet(() -> {
            newBoard.setId(id);
            return ResponseEntity.ok(repo.save(newBoard));
        });
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
