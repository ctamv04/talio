package server.controllers;

import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.repositories.TaskCardRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/tasks")
public class TaskCardController {
    private final TaskCardRepository repo;

    public TaskCardController( TaskCardRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = {"", "/"})
    public List<TaskCard> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskCard> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<TaskCard> add(@RequestBody TaskCard task) {
        return ResponseEntity.ok(repo.save(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskCard> update(@PathVariable("id") long id, @RequestBody TaskCard task) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)) {
            add(task);
        }

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
