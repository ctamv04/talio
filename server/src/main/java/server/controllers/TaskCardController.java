package server.controllers;

import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.repositories.TaskCardRepository;

import java.util.List;

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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
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
    public ResponseEntity<TaskCard> update(@PathVariable("id") long id,
                                           @RequestBody TaskCard newTask) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return repo.findById(id).map(task -> {
            task.setName(newTask.getName());
            return ResponseEntity.ok(repo.save(task));
        }).orElseGet(() -> {
            newTask.setId(id);
            return ResponseEntity.ok(repo.save(newTask));
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
