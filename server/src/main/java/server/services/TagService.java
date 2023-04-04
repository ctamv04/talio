package server.services;

import models.Board;
import models.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import server.repositories.BoardRepository;
import server.repositories.TagRepository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class TagService {
    private final TagRepository repo;
    private final BoardRepository repoBoard;

    /**
     * Instantiation of TagController using Dependency Injection
     *
     * @param repo TagRepository DI
     * @param repoBoard BoardRepository DI
     */
    public TagService(TagRepository repo,
                      BoardRepository repoBoard) {
        this.repo = repo;
        this.repoBoard = repoBoard;
    }

    /**
     * Update Tag
     *
     * @param tagID Tag ID
     * @param newTag New Tag
     * @return HTTP confirmation
     */
    @Transactional
    public ResponseEntity<Tag> update(@PathVariable("id") Long tagID,
                                           @RequestBody Tag newTag) {

        return repo.findById(tagID).map(tag -> {

            tag.setName(newTag.getName());
            tag.setColor(newTag.getColor());

            return ResponseEntity.ok(repo.save(tag));

        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Add Tag
     *
     * @param tag Tag
     * @param boardID Board ID
     * @return HTTP confirmation
     */
    @Transactional
    public ResponseEntity<Tag> add(Tag tag, Long boardID) {

        Optional<Board> optional = repoBoard.findById(boardID);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Board board = optional.get();
        board.getTags().add(tag);
        tag.setBoard(board);

        return ResponseEntity.ok(repo.save(tag));
    }

    /**
     * Delete Tag
     *
     * @param tagID Tag ID
     * @return HTTP confirmation
     */
    @Transactional
    public ResponseEntity<Tag> delete(Long tagID) {

        Optional<Tag> optional = repo.findById(tagID);
        if(optional.isEmpty())
            return ResponseEntity.badRequest().build();

        Tag tag = optional.get();

        tag.getTasks().forEach(task -> {
            task.getTags().remove(tag);
        });

        tag.getBoard().getTags().remove(tag);

        repo.deleteById(tagID);
        return ResponseEntity.ok().build();
    }
}
