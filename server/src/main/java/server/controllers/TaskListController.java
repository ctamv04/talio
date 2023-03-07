package server.controllers;

import java.util.List;

import models.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.repositories.TaskListRepository;

@RestController
@RequestMapping("/api/tasklists")
public class TaskListController {

    private final TaskListRepository repo;

    public TaskListController(TaskListRepository repo) {
        this.repo = repo;
    }

    //Draft method with multiboard support. b_id is Multiboard ID.
    // In the Tasklist DB model the ID of the Multiboard it belongs to
    //should also be an attribute
    //    @GetMapping(path = { "", "/{b_id}/" })
    //    public List<Tasklist> getAll() {
    //        return repo.findById(b_id).get();
    //    }

    @GetMapping(path = { "", "/" })
    public List<TaskList> getAll() {
        return repo.findAll();
    }

    //Draft method with multiboard support.
    //    @GetMapping("/{b_id}/{id}") //not interested in b_id here. I don't know if this will work
    //    public ResponseEntity<Tasklist> getById(@PathVariable("id") long id) {
    //        if (id < 0 || !repo.existsById(id)) {
    //            return ResponseEntity.badRequest().build();
    //        }
    //        return ResponseEntity.ok(repo.findById(id).get());
    //    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getById(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<TaskList> add(@RequestBody TaskList list) {
        return ResponseEntity.ok(repo.save(list));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskList> update(@PathVariable("id") long id,
                                           @RequestBody TaskList newList) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return repo.findById(id).map(list -> {
            list.setName(newList.getName());
            list.setTaskCards(newList.getTaskCards());
            return ResponseEntity.ok(repo.save(list));
        }).orElseGet(() -> {
            newList.setId(id);
            return ResponseEntity.ok(repo.save(newList));
        });
    }

    @PostMapping("/{id}")
    public ResponseEntity<TaskList> delete(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);

        return ResponseEntity.ok().build();
    }
}