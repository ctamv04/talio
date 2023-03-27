package server.controllers;

import models.TaskList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import models.Board;
import org.springframework.web.context.request.async.DeferredResult;
import server.repositories.BoardRepository;
import server.services.BoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository repo;
    private final BoardService service;

    /**
     * Constructor Method
     * @param repo The injected repository of the object
     * @param service The injected service of the object
     */
    public BoardController(BoardRepository repo, BoardService service) {
        this.repo = repo;
        this.service = service;
    }

    /**
     * Fetch all Method
     * @return All the existing boards
     */
    @GetMapping("")
    public List<Board> getAll() {
        return repo.findAll();
    }

    /**
     * Find by id method
     * @param id The id of the wanted board
     * @return A response based on the existence of the board
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable Long id) {
        Optional<Board> board=repo.findById(id);
        return board.map(ResponseEntity::ok).
                        orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Get task lists ids of the board
     * @param id The id of the board containing the task lists
     * @return A list of the task lists belonging to the board
     */
    @GetMapping("/{id}/tasklists")
    public ResponseEntity<List<Long>> getTaskListsId(@PathVariable Long id){
        Optional<Board> board=repo.findById(id);
        if(board.isEmpty())
            return ResponseEntity.badRequest().build();
        List<Long> lists=new ArrayList<>();
        for(var x: board.get().getTaskLists())
            lists.add(x.getId());
        return ResponseEntity.ok(lists);
    }

    /**
     * Get task lists of the board
     * @param id The id of the board containing the task lists
     * @return A list of the task lists belonging to the board
     */
    @GetMapping("/taskLists/{id}")
    public ResponseEntity<List<TaskList>> getTaskLists(@PathVariable Long id) {
        Optional<Board> board = repo.findById(id);
        return board.map(value -> ResponseEntity.ok(value.getTaskLists())).
                        orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a new board method
     * @param board The new board
     * @return A positive response
     */
    @PostMapping("")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return ResponseEntity.ok(repo.save(board));
    }

    /**
     * Update an existing board
     * @param id The id of the current board
     * @param newBoard The new board
     * @return A response based on the existence of the board
     */
    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable("id") Long id, @RequestBody Board newBoard) {
        ResponseEntity<Board> response= service.update(id,newBoard);

        if(response.getStatusCodeValue()!=200)
            return response;

        Map<Object,Consumer<Board>> listeners= detailsListener.get(id);
        if(listeners!=null){
            listeners.forEach((key,consumer)->{
                consumer.accept(response.getBody());
                listeners.remove(key);
            });
        }
        return response;
    }

    /**
     * Delete an existing board
     * @param id The id of the current board
     * @return A response based on the existence of the board
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable("id") Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.badRequest().build();
        repo.deleteById(id);

        Map<Object,Consumer<Board>> listeners= detailsListener.get(id);
        if(listeners!=null){
            listeners.forEach((key,consumer)->{
                consumer.accept(null);
                listeners.remove(key);
            });
        }

        return ResponseEntity.ok().build();
    }

    private final Map<Long, Map<Object,Consumer<Board>>> detailsListener=new ConcurrentHashMap<>();

    @GetMapping("/{id}/details-updates")
    public DeferredResult<ResponseEntity<Board>> getDetailsUpdates(@PathVariable("id") Long id){
        var noContent=ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res=new DeferredResult<ResponseEntity<Board>>(10000L,noContent);

        var key=new Object();
        Map<Object,Consumer<Board>> listeners=detailsListener.get(id);
        if(listeners==null){
            listeners=new ConcurrentHashMap<>();
            detailsListener.put(id,listeners);
        }
        listeners.put(key,board-> {
            if(board==null)
                res.setResult(ResponseEntity.badRequest().build());
            res.setResult(ResponseEntity.ok(board));
        });
        res.onCompletion(()-> detailsListener.get(id).remove(key));

        return res;
    }
}
