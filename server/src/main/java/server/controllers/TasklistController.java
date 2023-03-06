package server.controllers;

import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.Quote;
import server.repositories.QuoteRepository;

public class TasklistController {

    private final TasklistRepository repo;
    private int default_id = 0;

    public TasklistController(TasklistRepository repo) {
        this.repo = repo;
    }

    //b_id is Multiboard ID. In the Tasklist DB model the ID of the Multiboard it belongs to
    //should also be an attribute
    @GetMapping(path = { "", "/{b_id}/" })
    public List<Tasklist> getAll() {
        return repo.findById(b_id).get();
    }

    @GetMapping("/{b_id}/{id}") //not interested in b_id here. I don't know if this will work
    public ResponseEntity<Tasklist> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PostMapping(path = { "", "/{b_id}/" })
    public ResponseEntity<Tasklist> add(@PathVariable("id") long id, @RequestBody Tasklist list) {

        //User can create empty lists so I don't see how this part is necessary yet, unless we introduce
        //new limits later on.
//        if (list.name == null || isNullOrEmpty(quote.person.firstName) || isNullOrEmpty(quote.person.lastName)
//                || isNullOrEmpty(quote.quote)) {
//            return ResponseEntity.badRequest().build();
//        }

        list.b_id = id;
        list.name = "Tasklist " + default_id;
        default_id++;
        Tasklist saved = repo.save(list);
        return ResponseEntity.ok(saved);
    }

    @PostMapping(path = { "", "/{b_id}/" })
    public ResponseEntity<Tasklist> delete(@PathVariable("id") long id) {

        Tasklist del = repo.findById(id).get().get(0);
        repo.delete(del);
        return ResponseEntity.ok(del);
    }

    @PostMapping(path = { "", "/{b_id}/" })
    public ResponseEntity<Tasklist> update(@PathVariable("id") long id, @RequestBody String name) {

        Tasklist edit = repo.findById(id).get().get(0);
        repo.delete(edit);
        edit.name = name;
        Tasklist saved = repo.save(edit);
        return ResponseEntity.ok(saved);
    }
}