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
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagRepository repo;
    private final TaskCardRepository repoCard;
    private final TagService service;

    /**
     * Instantiation of TagController using Dependency Injection
     *
     * @param repo TagRepository DI
     * @param service TagService DI
     * @param repoCard TaskCardRepository DI
     */
    public TagController(TagRepository repo, TagService service, TaskCardRepository repoCard) {

        this.repo = repo;
        this.service = service;
        this.repoCard = repoCard;

    }

    /**
     * Get Board Tags of some TaskCard
     *
     * @param cardID TaskCard ID
     * @return List of Tags
     */
    @GetMapping("/board/{id}")
    public ResponseEntity<List<Tag>> getBoardTags(@PathVariable("id") Long cardID) {

        Optional<TaskCard> taskCard = repoCard.findById(cardID);

        if (taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTaskList().getBoard().getTags());

    }

    /**
     * Get TaskCard Tags of some TaskCard
     *
     * @param cardID TaskCard ID
     * @return Set of Tags
     */
    @GetMapping("/task/{id}")
    public ResponseEntity<Set<Tag>> getTaskTags(@PathVariable("id") Long cardID) {

        Optional<TaskCard> taskCard = repoCard.findById(cardID);

        if (taskCard.isEmpty())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(taskCard.get().getTags());

    }

    /**
     * Get all Tags
     *
     * @return List of Tags
     */
    @GetMapping("")
    public List<Tag> getAll() {
        return repo.findAll();
    }

    /**
     * Get Tag by ID
     *
     * @param tagID Tag ID
     * @return Tag
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(@PathVariable("id") Long tagID) {

        Optional<Tag> tag = repo.findById(tagID);
        if (tag.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(tag.get());
    }

    /**
     * Add Tag to A bOARD
     *
     * @param tag Tag
     * @param boardId Board ID
     * @return HTTP confirmation
     */
    @PostMapping("")
    public ResponseEntity<Tag> add(@RequestBody Tag tag,
                                   @PathParam("boardId") Long boardId) {
        return service.add(tag, boardId);
    }

    /**
     * Update Tag
     *
     * @param tagID Tag ID
     * @param newTag New Tag
     * @return HTTP confirmation
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable("id") Long tagID,
                                      @RequestBody Tag newTag) {

        return service.update(tagID, newTag);
    }

    /**
     * Delete Tag
     *
     * @param tagID Tag ID
     * @return HTTP confirmation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Tag> delete(@PathVariable("id") Long tagID) {

        return service.delete(tagID);
    }

}
