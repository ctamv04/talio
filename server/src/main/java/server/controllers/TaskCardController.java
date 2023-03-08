package server.controllers;

import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.repositories.TaskCardRepository;
import server.services.TaskCardService;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskCardController {
    private final TaskCardRepository taskCardRepository;
    private final TaskCardService taskCardService;

    public TaskCardController(TaskCardRepository taskCardRepository, TaskCardService taskCardService) {
        this.taskCardRepository = taskCardRepository;
        this.taskCardService = taskCardService;
    }

    @GetMapping("")
    public List<TaskCard> getAll() {
        return taskCardRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskCard> getById(@PathVariable("id") long id) {
        Optional<TaskCard> taskCard=taskCardRepository.findById(id);
        if(taskCard.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(taskCard.get());
    }

    @PostMapping("")
    public ResponseEntity<TaskCard> add(@RequestBody TaskCard taskCard, @PathParam("taskListId") Long taskListId) {
        return taskCardService.add(taskCard,taskListId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskCard> update(@PathVariable("id") Long id,
                                           @RequestBody TaskCard newTask) {
        return taskCardService.update(id,newTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") long id) {
        if (!taskCardRepository.existsById(id))
            return ResponseEntity.badRequest().build();
        taskCardRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
