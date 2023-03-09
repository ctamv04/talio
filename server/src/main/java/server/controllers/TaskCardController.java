package server.controllers;

import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.TaskCardService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskCardController {
    private final TaskCardService service;

    public TaskCardController(TaskCardService service) {
        this.service = service;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @GetMapping("/{id}")
    public ResponseEntity<TaskCard> getById(@PathVariable("id") long id) {
        return service.getById(id);
    }

    @GetMapping(path = {"", "/"})
    public List<TaskCard> getAll() {
        return service.findAll();
    }

    @PostMapping(path = {"", "/{id}/"})
    public ResponseEntity<TaskCard> add(@RequestBody TaskCard task) {
        return service.add(task, task.getTaskList().getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") long id) {
        return service.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskCard> update(@PathVariable("id") long id,
                                           @RequestBody TaskCard newTask) {
        return service.update(id, newTask);
    }
}
