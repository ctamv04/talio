package server.controllers;

import models.Tag;
import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.repositories.TaskCardRepository;
import server.services.TaskCardService;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TaskCardRepository repo;
    private final TaskCardService service;

    /**
     * Constructor Method
     * @param repo The injected repo of the object
     * @param service The injected service of the object
     */
    public TagController(TaskCardRepository repo, TaskCardService service) {
        this.repo = repo;
        this.service = service;
    }

    /**
     *
     * @param cardID
     * @return
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<List<Tag>> getBoardTags(@PathVariable("id") Long cardID){
        Optional<TaskCard> taskCard = repo.findById(cardID);

        if(taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTaskList().getBoard().getTags());

    }

    /**
     *
     * @param cardID
     * @return
     */
    @GetMapping("/task/{id}")
    public ResponseEntity<List<Tag>> getTaskTags(@PathVariable("id") Long cardID){
        Optional<TaskCard> taskCard = repo.findById(cardID);

        if(taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTags());

    }

}