package server.controllers;

import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;
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
    private final TaskCardRepository taskCardRepository;
    private final TaskCardService taskCardService;
    private final LongPollingService longPollingService;
    private final TaskListRepository taskListRepository;
    private final SimpMessagingTemplate messages;

    /**
     * Constructor Method
     * @param repo The repo of the object
     * @param service The service of the object
     * @param longPollingService The longPollingService of the object
     * @param taskListRepository The taskListRepository of the object
     * @param messages The messaging template
     */
    public TaskCardController(TaskCardRepository repo, TaskCardService service, LongPollingService longPollingService,
                              TaskListRepository taskListRepository, SimpMessagingTemplate messages) {
        this.taskCardRepository = repo;
        this.taskCardService = service;
        this.longPollingService = longPollingService;
        this.taskListRepository = taskListRepository;
        this.messages = messages;
    }

    /**
     * Fetch all method
     * @return All the existing taskCards
     */
    @GetMapping("")
    public List<TaskCard> getAll() {
        return taskCardRepository.findAll();
    }

    /**
     * Find by id method
     * @param id The id of the wanted taskCard
     * @return A response based on the existence of the taskCard
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskCard> getById(@PathVariable("id") Long id) {
        Optional<TaskCard> taskCard= taskCardRepository.findById(id);
        if(taskCard.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(taskCard.get());
    }

    /**
     * Get the card by its id
     * @param cardID The card id
     * @return The corresponding card
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<Long> getBoardId(@PathVariable("id") Long cardID){
        Optional<TaskCard> taskCard = taskCardRepository.findById(cardID);

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
        ResponseEntity<TaskCard> response= taskCardService.add(taskCard,taskListId);
        if(response.getStatusCodeValue()!=200)
            return response;

        TaskCard newTaskCard=response.getBody();
        if(newTaskCard==null)
            return response;

        longPollingService.registerUpdate(idsListeners.get(taskListId),
                taskCardService.convertTaskCardsToIds(taskListRepository.getTaskCardsId(taskListId)));

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
        ResponseEntity<TaskCard> response= taskCardService.update(id, newTaskCard);
        if(response.getStatusCodeValue()!=200)
            return response;

        TaskCard taskCard=response.getBody();
        if(taskCard==null)
            return response;
        messages.convertAndSend("/topic/taskcard/"+id,taskCard);

        return response;
    }

    @PutMapping("/swap/{id}/{pos}")
    public ResponseEntity<TaskCard> swapBetweenLists(@PathVariable("id") Long id, @PathVariable("pos") int pos,
                                                     @PathParam("list1") Long list1, @PathParam("list2") Long list2) {
        return taskCardService.swapBetweenLists(id,pos,list1,list2, idsListeners);
    }

    /**
     * Delete an existing taskCard
     * @param id The current taskCard
     * @return A response based on the existence of the taskCard
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskCard> delete(@PathVariable("id") Long id) {
        Optional<TaskCard> optional = taskCardRepository.findById(id);
        if(optional.isEmpty())
            return ResponseEntity.badRequest().build();
        TaskCard taskCard=optional.get();
        ResponseEntity<TaskCard> response= taskCardService.delete(id);
        if(response.getStatusCodeValue()!=200)
            return response;

        TaskCard dummyTaskcard=new TaskCard();
        dummyTaskcard.setPosition(-1);
        messages.convertAndSend("/topic/taskcard/"+id,dummyTaskcard);
        messages.convertAndSend("/topic/extended-taskcard/"+id,dummyTaskcard);
        longPollingService.registerUpdate(idsListeners.get(taskCard.getTaskList().getId()),
                taskCardService.convertTaskCardsToIds(taskListRepository.getTaskCardsId(taskCard.getTaskList().getId())));

        return response;
    }

    private final Map<Long, Map<Object, Consumer<List<Long>>>> idsListeners =new ConcurrentHashMap<>();

    @GetMapping("/{id}/ids-updates")
    public DeferredResult<ResponseEntity<List<Long>>> getIdsUpdates(@PathVariable("id") Long taskListId){
        return longPollingService.getUpdates(taskListId, idsListeners);
    }
}
