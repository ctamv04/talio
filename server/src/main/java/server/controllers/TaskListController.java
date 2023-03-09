package server.controllers;

import java.util.List;
import java.util.Optional;

import models.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.repositories.TaskListRepository;
import server.services.TaskListService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/tasklists")
public class TaskListController {

    private final TaskListRepository taskListRepository;
    private final TaskListService taskListService;

    public TaskListController(TaskListRepository taskListRepository, TaskListService taskListService) {
        this.taskListRepository = taskListRepository;
        this.taskListService = taskListService;
    }

    @GetMapping("")
    public List<TaskList> getAll() {
        return taskListRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskList> findById(@PathVariable("id") Long id) {
        Optional<TaskList> taskList=taskListRepository.findById(id);
        if(taskList.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(taskList.get());
    }

    @PostMapping("")
    public ResponseEntity<TaskList> add(@RequestBody TaskList taskList, @PathParam("boardId") Long boardId) {
        return taskListService.add(taskList,boardId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskList> update(@PathVariable("id") Long id,
                                           @RequestBody TaskList newTaskList) {
        return taskListService.update(id,newTaskList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskList> delete(@PathVariable("id") Long id) {
        if (!taskListRepository.existsById(id))
            return ResponseEntity.badRequest().build();
        taskListRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}