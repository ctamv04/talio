package server.controllers;

import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.repositories.TaskCardRepository;
import server.services.LongPollingService;
import server.services.TaskCardService;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/tasks")
public class TaskCardController {
    private final TaskCardRepository repo;
    private final TaskCardService service;
    private final LongPollingService longPollingService;

    /**
     * Constructor Method
     *
     * @param repo               The injected repo of the object
     * @param service            The injected service of the object
     * @param longPollingService The injected long polling service
     */
    public TaskCardController(TaskCardRepository repo, TaskCardService service, LongPollingService longPollingService) {
        this.repo = repo;
        this.service = service;
        this.longPollingService = longPollingService;
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
     *
     * @param cardID
     * @return
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<Long> getBoardId(@PathVariable("id") Long cardID){
        Optional<TaskCard> taskCard = repo.findById(cardID);

        if(taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTaskList().getBoard().getId());

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
        ResponseEntity<TaskCard> response= service.add(taskCard,taskListId);
        if(response.getStatusCodeValue()!=200)
            return response;
        TaskCard newTaskCard=response.getBody();
        if(newTaskCard==null)
            return response;

        service.traverseIdsListeners(idsListeners.get(newTaskCard.getTaskList().getId()),taskCard.getTaskList());

        return response;
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

    @PutMapping("/swap/{id}/{pos}")
    public ResponseEntity<TaskCard> swapBetweenLists(@PathVariable("id") Long id, @PathVariable("pos") int pos,
                                           @PathParam("list1") Long list1, @PathParam("list2") Long list2) {
        return service.swapBetweenLists(id,pos,list1,list2, idsListeners);
    }

    /**
     * Delete an existing taskCard
     * @param id The current taskCard
     * @return A response based on the existence of the taskCard
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") Long id) {
        Optional<TaskCard> optional =repo.findById(id);
        if(optional.isEmpty())
            return ResponseEntity.badRequest().build();
        TaskCard taskCard=optional.get();
        ResponseEntity<TaskCard> response=service.delete(id);
        if(response.getStatusCodeValue()!=200)
            return response;

        service.traverseIdsListeners(idsListeners.get(taskCard.getTaskList().getId()),taskCard.getTaskList());

        return response;
    }

    private final Map<Long, Map<Object, Consumer<List<Long>>>> idsListeners =new ConcurrentHashMap<>();

    @GetMapping("/{id}/ids-updates")
    public DeferredResult<ResponseEntity<List<Long>>> getIdsUpdates(@PathVariable("id") Long taskListId){
        return longPollingService.getIdsUpdates(taskListId, idsListeners);
    }
}
