package server.controllers;

import models.Board;
import models.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.repositories.BoardRepository;
import server.services.BoardService;
import server.services.LongPollingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final LongPollingService longPollingService;

    /**
     * Constructor Method
     *
     * @param boardRepository    The boardRepository of the object
     * @param boardService       The boardService of the object
     * @param longPollingService The longPollingService of the object
     */
    public BoardController(BoardRepository boardRepository, BoardService boardService, LongPollingService longPollingService) {
        this.boardRepository = boardRepository;
        this.boardService = boardService;
        this.longPollingService = longPollingService;
    }

    /**
     * Fetch all Method
     *
     * @return All the existing boards
     */
    @GetMapping("")
    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    /**
     * Find by id method
     *
     * @param id The id of the wanted board
     * @return A response based on the existence of the board
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getById(@PathVariable Long id) {
        Optional<Board> board = boardRepository.findById(id);
        return board.map(ResponseEntity::ok).
                orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Get task lists ids of the board
     *
     * @param id The id of the board containing the task lists
     * @return A list of the task lists belonging to the board
     */
    @GetMapping("/{id}/tasklists")
    public ResponseEntity<List<Long>> getTaskListsId(@PathVariable Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty())
            return ResponseEntity.badRequest().build();
        List<Long> lists = new ArrayList<>();
        for (var x : board.get().getTaskLists())
            lists.add(x.getId());
        return ResponseEntity.ok(lists);
    }

    /**
     * Get task lists of the board
     *
     * @param id The id of the board containing the task lists
     * @return A list of the task lists belonging to the board
     */
    @GetMapping("/taskLists/{id}")
    public ResponseEntity<List<TaskList>> getTaskLists(@PathVariable Long id) {
        Optional<Board> board = boardRepository.findById(id);
        return board.map(value -> ResponseEntity.ok(value.getTaskLists())).
                orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a new board method
     *
     * @param board The new board
     * @return A positive response
     */
    @PostMapping("")
    public ResponseEntity<Board> add(@RequestBody Board board) {
        return ResponseEntity.ok(boardRepository.save(board));
    }

    /**
     * Update an existing board
     *
     * @param id       The id of the current board
     * @param newBoard The new board
     * @return A response based on the existence of the board
     */
    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable("id") Long id, @RequestBody Board newBoard) {
        ResponseEntity<Board> response = boardService.update(id, newBoard);
        if (response.getStatusCodeValue() != 200)
            return response;

        longPollingService.registerUpdate(detailsListeners.get(id), response.getBody());

        return response;
    }

    /**
     * Delete an existing board
     *
     * @param id The id of the current board
     * @return A response based on the existence of the board
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable("id") Long id) {
        if (!boardRepository.existsById(id))
            return ResponseEntity.badRequest().build();
        boardRepository.deleteById(id);

        longPollingService.registerUpdate(detailsListeners.get(id), null);

        return ResponseEntity.ok().build();
    }

    private final Map<Long, Map<Object, Consumer<Board>>> detailsListeners = new ConcurrentHashMap<>();

    @GetMapping("/{id}/details-updates")
    public DeferredResult<ResponseEntity<Board>> getDetailsUpdates(@PathVariable("id") Long id) {
        return longPollingService.getUpdates(id, detailsListeners);
    }
}
