package server.services;

import models.TaskCard;
import models.TaskList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;

import java.util.Optional;

import static java.lang.Math.min;


@Service
public class TaskCardService {
    private final TaskCardRepository taskCardRepository;
    private final TaskListRepository taskListRepository;

    /**
     * Constructor Method
     * @param taskCardRepository The injected taskCardRepository of the object
     * @param taskListRepository The injected taskListRepository of the object
     */
    public TaskCardService(TaskCardRepository taskCardRepository,
                           TaskListRepository taskListRepository) {
        this.taskCardRepository = taskCardRepository;
        this.taskListRepository = taskListRepository;
    }

    /**
     * Update an taskCard
     * @param id The id of the taskCard
     * @param newTaskCard The new taskCard
     * @return A response based on the existence of the taskCard
     */
    @Transactional
    public ResponseEntity<TaskCard> update(@PathVariable("id") Long id,
                                           @RequestBody TaskCard newTaskCard) {
        return taskCardRepository.findById(id).map(task -> {
            task.setName(newTaskCard.getName());
            task.setDescription(newTaskCard.getDescription());
            task.setPosition(newTaskCard.getPosition());
            return ResponseEntity.ok(taskCardRepository.save(task));
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add a new taskCard
     * @param taskCard The wanted taskCard
     * @param taskListId The list it belongs to
     * @return A response based on the existence of the list
     */
    @Transactional
    public ResponseEntity<TaskCard> add(TaskCard taskCard, Long taskListId) {
        Optional<TaskList> optional = taskListRepository.findById(taskListId);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        TaskList taskList = optional.get();
        taskList.getTaskCards().add(taskCard);
        taskCard.setTaskList(taskList);
        return ResponseEntity.ok(taskCardRepository.save(taskCard));
    }

    @Transactional
    public ResponseEntity<TaskCard> swapBetweenLists(Long id, int pos, Long idList1, Long idList2) {
        Optional<TaskCard> optional=taskCardRepository.findById(id);
        if(optional.isEmpty() || pos<0)
            return ResponseEntity.badRequest().build();
        Optional<TaskList> optionalTaskList1=taskListRepository.findById(idList1);
        Optional<TaskList> optionalTaskList2=taskListRepository.findById(idList2);
        if(optionalTaskList1.isEmpty() || optionalTaskList2.isEmpty())
            return ResponseEntity.badRequest().build();

        TaskCard taskCard=optional.get();
        TaskList taskList1=optionalTaskList1.get();
        TaskList taskList2=optionalTaskList2.get();
        if(!taskList1.getTaskCards().contains(taskCard))
            return ResponseEntity.badRequest().build();

        int index=taskList1.getTaskCards().indexOf(taskCard);
        taskList1.getTaskCards().remove(taskCard);
        pos=min(pos,taskList2.getTaskCards().size());

        taskCard.setTaskList(taskList2);
        taskCard.setPosition(pos);
        for(int i=index;i<taskList1.getTaskCards().size();i++)
            taskList1.getTaskCards().get(i).setPosition(i);
        taskList2.getTaskCards().add(pos,taskCard);
        for(int i=pos+1;i<taskList2.getTaskCards().size();i++)
            taskList2.getTaskCards().get(i).setPosition(i);

        taskListRepository.save(taskList1);
        taskListRepository.save(taskList2);
        return ResponseEntity.ok(taskCardRepository.save(taskCard));
    }
}
