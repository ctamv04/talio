package server.controllers;

import models.Tag;
import models.TaskCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.repositories.TagRepository;
import server.repositories.TaskCardRepository;
import server.services.TagService;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagRepository repo;
    private final TaskCardRepository repoCard;
    private final TagService service;

    /**
     * Constructor Method
     *
     * @param repoCard The injected repo of the object
     * @param service  The injected service of the object
     */
    public TagController(TagRepository repo, TagService service, TaskCardRepository repoCard) {

        this.repo = repo;
        this.service = service;
        this.repoCard = repoCard;

    }

    /**
     * Returns the Tag List of the Board a TaskCard belongs to
     *
     * @param cardID ID of the TaskCard
     * @return
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<List<Tag>> getBoardTags(@PathVariable("id") Long cardID) {

        Optional<TaskCard> taskCard = repoCard.findById(cardID);

        if (taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTaskList().getBoard().getTags());

    }

    /**
     * Returns the Tag List of a TaskCard
     *
     * @param cardID
     * @return
     */
    @GetMapping("/task/{id}")
    public ResponseEntity<List<Tag>> getTaskTags(@PathVariable("id") Long cardID) {

        Optional<TaskCard> taskCard = repoCard.findById(cardID);

        if (taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTags());

    }

    /**
     * @return
     */
    @GetMapping("")
    public List<Tag> getAll() {
        return repo.findAll();
    }

    /**
     * @param tagID
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(@PathVariable("id") Long tagID) {

        Optional<Tag> tag = repo.findById(tagID);
        if (tag.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(tag.get());
    }

    /**
     * @param tag
     * @param boardID
     * @return
     */
    @PostMapping("")
    public ResponseEntity<Tag> add(@RequestBody Tag tag,
                                   @PathParam("boardId") Long boardID) {

        return service.add(tag, boardID);
    }

    /**
     * @param tagID
     * @param newTag
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable("id") Long tagID,
                                      @RequestBody Tag newTag) {

        return service.update(tagID, newTag);
    }

    /**
     * @param tagID
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Tag> delete(@PathVariable("id") Long tagID) {

        return service.delete(tagID);
    }

}
