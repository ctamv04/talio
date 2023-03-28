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

import java.util.List;
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
            task.setSubs(newTaskCard.getSubs());
            task.setBackID(newTaskCard.getBackID());
            task.setFontID(newTaskCard.getFontID());
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
        taskCard.setPosition(taskList.getTaskCards().size()-1);
        return ResponseEntity.ok(taskCardRepository.save(taskCard));
    }

    @Transactional
    public ResponseEntity<TaskCard> swapBetweenLists(Long id, int pos, Long idList1, Long idList2) {
        Optional<TaskCard> optional=taskCardRepository.findById(id);
        Optional<TaskList> optionalTaskList1=taskListRepository.findById(idList1);
        Optional<TaskList> optionalTaskList2=taskListRepository.findById(idList2);
        if(optional.isEmpty() || pos<0 || optionalTaskList1.isEmpty() || optionalTaskList2.isEmpty())
            return ResponseEntity.badRequest().build();

        TaskCard taskCard=optional.get();
        TaskList taskList1=optionalTaskList1.get();
        TaskList taskList2=optionalTaskList2.get();
        if(!taskList1.getTaskCards().contains(taskCard))
            return ResponseEntity.badRequest().build();

        if(idList1.equals(idList2))
            return swapSameList(taskCard,taskList1,pos);
        pos=min(pos,taskList2.getTaskCards().size());

        taskList1.getTaskCards().remove(taskCard);
        taskList2.getTaskCards().add(taskCard);

        List<TaskCard> orderedTaskCardList1=taskListRepository.getTaskCardsId(idList1);
        List<TaskCard> orderedTaskCardList2=taskListRepository.getTaskCardsId(idList2);
        int index=orderedTaskCardList1.indexOf(taskCard);

        for(int i=index+1;i<orderedTaskCardList1.size();i++)
            orderedTaskCardList1.get(i).setPosition(i-1);
        for(int i=pos;i<orderedTaskCardList2.size();i++)
            orderedTaskCardList2.get(i).setPosition(i+1);
        taskCard.setTaskList(taskList2);
        taskCard.setPosition(pos);
        taskListRepository.save(taskList1);
        taskListRepository.save(taskList2);
        return ResponseEntity.ok(taskCardRepository.save(taskCard));
    }

    private ResponseEntity<TaskCard> swapSameList(TaskCard taskCard, TaskList taskList, int pos){
        List<TaskCard> orderedTaskCardList=taskListRepository.getTaskCardsId(taskList.getId());
        pos=min(pos,taskList.getTaskCards().size()-1);
        int index=orderedTaskCardList.indexOf(taskCard);
        if(index<pos)
            for(int i=index+1;i<=pos;i++)
                orderedTaskCardList.get(i).setPosition(i-1);
        else
            for(int i=pos;i<index;i++)
                orderedTaskCardList.get(i).setPosition(i+1);
        orderedTaskCardList.get(index).setPosition(pos);
        taskListRepository.save(taskList);
        return ResponseEntity.ok(taskCardRepository.save(taskCard));
    }

    @Transactional
    public ResponseEntity<TaskCard> delete(Long id) {
        Optional<TaskCard> optional=taskCardRepository.findById(id);
        if(optional.isEmpty())
            return ResponseEntity.badRequest().build();

        TaskCard taskCard=optional.get();
        TaskList taskList=taskCard.getTaskList();
        List<TaskCard> orderTaskCardList=taskListRepository.getTaskCardsId(taskList.getId());
        int index=orderTaskCardList.indexOf(taskCard);
        taskList.getTaskCards().remove(taskCard);
        for(int i=index+1;i<orderTaskCardList.size();i++)
            orderTaskCardList.get(i).setPosition(i-1);
        taskCardRepository.delete(taskCard);
        taskListRepository.save(taskList);
        return ResponseEntity.ok().build();
    }
}
