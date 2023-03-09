package server.controllers;

import models.TaskList;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.repositories.TaskListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestTaskListRepository implements TaskListRepository {
    public final List<TaskList> taskLists = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    public List<TaskList> findAll() {
        call("findAll");
        return taskLists;
    }

    public List<TaskList> findAll(Sort sort) {
        return null;
    }

    public Page<TaskList> findAll(Pageable pageable) {
        return null;
    }

    public List<TaskList> findAllById(Iterable<Long> longs) {
        return null;
    }

    public long count() {
        return 0;
    }

    public void deleteById(Long aLong) {

    }

    public void delete(TaskList entity) {

    }

    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    public void deleteAll(Iterable<? extends TaskList> entities) {

    }

    public void deleteAll() {

    }

    public <S extends TaskList> S save(S entity) {
        call("save");
        entity.setId((long) taskLists.size() + 1);
        taskLists.add(entity);
        return entity;
    }

    public <S extends TaskList> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    public Optional<TaskList> findById(Long aLong) {
        for (TaskList taskList : taskLists) {
            if (taskList.getId().equals(aLong)) {
                return Optional.of(taskList);
            }
        }
        return Optional.empty();
    }

    public boolean existsById(Long aLong) {
        for (TaskList taskList : taskLists) {
            if (taskList.getId().equals(aLong)) {
                return true;
            }
        }
        return false;
    }

    public void flush() {

    }

    public <S extends TaskList> S saveAndFlush(S entity) {
        return null;
    }

    public <S extends TaskList> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    public void deleteAllInBatch(Iterable<TaskList> entities) {

    }

    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    public void deleteAllInBatch() {

    }

    public TaskList getOne(Long aLong) {
        return null;
    }

    public TaskList getById(Long aLong) {
        return null;
    }

    public <S extends TaskList> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    public <S extends TaskList> List<S> findAll(Example<S> example) {
        return null;
    }

    public <S extends TaskList> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    public <S extends TaskList> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    public <S extends TaskList> long count(Example<S> example) {
        return 0;
    }

    public <S extends TaskList> boolean exists(Example<S> example) {
        return false;
    }

    public <S extends TaskList, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
