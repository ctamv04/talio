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
    private final TaskCardRepository repo;
    private final TaskCardService service;

    /**
     * Constructor Method
     * @param repo The injected repo of the object
     * @param service The injected service of the object
     */
    public TaskCardController(TaskCardRepository repo, TaskCardService service) {
        this.repo = repo;
        this.service = service;
    }

    /**
     * Fetch all method
     * @return All the existing taskCards
     */
    @GetMapping("")
    public List<TaskCard> getAll() {
        return repo.findAll();
    }

    /**
     * Find by id method
     * @param id The id of the wanted taskCard
     * @return A response based on the existence of the taskCard
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskCard> getById(@PathVariable("id") Long id) {
        Optional<TaskCard> taskCard=repo.findById(id);
        if(taskCard.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(taskCard.get());
    }

    /**
     * Add a taskCard method
     * @param taskCard The wanted taskCard
     * @param taskListId The id of the list it belongs to
     * @return A response based on the existence of the board
     */
    @PostMapping("")
    public ResponseEntity<TaskCard> add(@RequestBody TaskCard taskCard,
                                        @PathParam("taskListId") Long taskListId) {
        return service.add(taskCard,taskListId);
    }

    /**
     * Update an existing taskCard
     * @param id The id of the current taskCard
     * @param newTaskCard The new TaskCard
     * @return A response based on the existence of the taskCard
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskCard> update(@PathVariable("id") Long id,
                                           @RequestBody TaskCard newTaskCard) {
        return service.update(id, newTaskCard);
    }

    /**
     * Delete an existing taskCard
     * @param id The current taskCard
     * @return A response based on the existence of the taskCard
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.badRequest().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
