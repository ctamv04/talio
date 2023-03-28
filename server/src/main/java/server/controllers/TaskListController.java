package server.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import models.TaskCard;
import models.TaskList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.async.DeferredResult;
import server.repositories.TaskListRepository;
import server.services.TaskListService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/api/tasklists")
public class TaskListController {

    private final TaskListRepository taskListRepository;
    private final TaskListService taskListService;

    /**
     * Constructor Method
     * @param taskListRepository The injected taskListRepository of the object
     * @param taskListService The injected taskListService of the object
     */
    public TaskListController(TaskListRepository taskListRepository,
                              TaskListService taskListService) {

        this.taskListRepository = taskListRepository;
        this.taskListService = taskListService;
    }

    /**
     * Fetch all method
     * @return  All the existing taskLists
     */
    @GetMapping("")
    public List<TaskList> getAll() {
        return taskListRepository.findAll();
    }

    /**
     * Find by id method
     * @param id The id of the wanted taskList
     * @return A response based on the existence of the taskList
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getById(@PathVariable("id") Long id) {

        Optional<TaskList> taskList=taskListRepository.findById(id);
        return taskList.map(ResponseEntity::ok).
                            orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Get task cards ids of the task list
     * @param id The id of the task list containing the task cards
     * @return A list of the task cards belonging to the list
     */
    @GetMapping("/{id}/taskcards")
    public ResponseEntity<List<Long>> getTaskCardsId(@PathVariable Long id){
        Optional<TaskList> taskList=taskListRepository.findById(id);
        if(taskList.isEmpty())
            return ResponseEntity.badRequest().build();
        List<Long> ids=new ArrayList<>();
        List<TaskCard> taskCards=taskListRepository.getTaskCardsId(id);
        for(TaskCard taskCard: taskCards)
            ids.add(taskCard.getId());
        return ResponseEntity.ok(ids);
    }

    /**
     * Get task cards of the task list
     *
     * @param id The id of the task list containing the task cards
     * @return A list of the task cards belonging to the list
     */
    @GetMapping("/taskCards/{id}")
    public ResponseEntity<List<TaskCard>> getTaskCard(@PathVariable Long id) {
        Optional<TaskList> taskList = taskListRepository.findById(id);
        return taskList.map(value -> ResponseEntity.ok(value.getTaskCards())).
                orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a new taskList method
     * @param taskList The wanted taskList
     * @param boardId The board it belongs to
     * @return A response based on the existence of the board
     */
    @PostMapping("")
    public ResponseEntity<TaskList> add(@RequestBody TaskList taskList,
                                        @PathParam("boardId") Long boardId) {
        ResponseEntity<TaskList> response= taskListService.add(taskList,boardId);
        if(response.getStatusCodeValue()!=200)
            return response;
        TaskList newTaskList=response.getBody();
        if(newTaskList==null)
            return response;
        Map<Object,Consumer<List<Long>>> listeners= idsListener.get(newTaskList.getBoard().getId());
        if(listeners!=null){
            listeners.forEach((key,consumer)->{
                List<Long> ids=new ArrayList<>();
                for(var x: newTaskList.getBoard().getTaskLists())
                    ids.add(x.getId());
                consumer.accept(ids);
                listeners.remove(key);
            });
        }
        return response;
    }

    /**
     * Update a taskList method
     * @param id The current taskList
     * @param newTaskList The new taskList
     * @return A response based on the existence of the taskList
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskList> update(@PathVariable("id") Long id,
                                           @RequestBody TaskList newTaskList) {
        return taskListService.update(id,newTaskList);
    }

    /**
     * Delete a taskList method
     * @param id The id of the corresponding taskList
     * @return A response based on the existence of the taskList
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskList> delete(@PathVariable("id") Long id) {
        if (!taskListRepository.existsById(id))
            return ResponseEntity.badRequest().build();

        TaskList taskList=taskListRepository.getById(id);
        taskListRepository.deleteById(id);
        Map<Object,Consumer<List<Long>>> listeners= idsListener.get(taskList.getBoard().getId());
        if(listeners!=null){
            listeners.forEach((key,consumer)->{
                List<Long> ids=new ArrayList<>();
                for(var x: taskList.getBoard().getTaskLists()){
                    if(x!=taskList)
                        ids.add(x.getId());
                }
                consumer.accept(ids);
                listeners.remove(key);
            });
        }

        return ResponseEntity.ok().build();
    }

    private final Map<Long, Map<Object,Consumer<List<Long>>>> idsListener=new ConcurrentHashMap<>();

    @GetMapping("/{id}/ids-updates")
    public DeferredResult<ResponseEntity<List<Long>>> getIdsUpdates(@PathVariable("id") Long id){
        var noContent=ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res=new DeferredResult<ResponseEntity<List<Long>>>(10000L,noContent);

        var key=new Object();
        Map<Object, Consumer<List<Long>>> listeners=idsListener.get(id);
        if(listeners==null){
            listeners=new ConcurrentHashMap<>();
            idsListener.put(id,listeners);
        }
        listeners.put(key,list -> res.setResult(ResponseEntity.ok(list)));
        res.onCompletion(()-> idsListener.get(id).remove(key));

        return res;
    }
}