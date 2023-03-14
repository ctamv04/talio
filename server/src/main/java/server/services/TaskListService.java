package server.services;

import models.Board;
import models.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import server.repositories.BoardRepository;
import server.repositories.TaskListRepository;

import java.util.Optional;

@Service
public class TaskListService {
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    /**
     * Constructor Method
     * @param taskListRepository The injected taskListRepository of the object
     * @param boardRepository The injected boardRepository of the object
     */
    public TaskListService(TaskListRepository taskListRepository, BoardRepository boardRepository) {
        this.taskListRepository = taskListRepository;
        this.boardRepository = boardRepository;
    }

    /**
     * Update a taskList
     * @param id The id of the current taskList
     * @param newTaskList The new taskList
     * @return A response based on the existence of the taskList
     */
    @Transactional
    public ResponseEntity<TaskList> update(@PathVariable("id") Long id,
                                           @RequestBody TaskList newTaskList) {
        return taskListRepository.findById(id).map(list -> {
            list.setName(newTaskList.getName());
            return ResponseEntity.ok(taskListRepository.save(list));
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a new taskList
     * @param taskList The wanted taskList
     * @param boardId The board it belongs to
     * @return A response based on the existence of the board
     */
    @Transactional
    public ResponseEntity<TaskList> add(TaskList taskList, Long boardId) {
        Optional<Board> optional=boardRepository.findById(boardId);
        if(optional.isEmpty())
            return ResponseEntity.badRequest().build();
        Board board=optional.get();
        board.getTaskLists().add(taskList);
        taskList.setBoard(board);
        return ResponseEntity.ok(taskListRepository.save(taskList));
    }
}
