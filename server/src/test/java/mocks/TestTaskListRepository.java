package mocks;

import models.TaskCard;
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

@SuppressWarnings("all")
public class TestTaskListRepository implements TaskListRepository {
    private long counter = 0;
    private final List<TaskList> lists = new ArrayList<>();
    private final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<TaskList> findAll() {
        call("findAll");
        return lists;
    }

    @Override
    public List<TaskList> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<TaskList> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<TaskList> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        lists.removeIf(list -> list.getId().equals(aLong));
    }

    @Override
    public void delete(TaskList entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends TaskList> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends TaskList> S save(S entity) {
        call("save");
        if (entity.getId() != null) {
            Optional<TaskList> opt = findById(entity.getId());
            if (opt.isPresent()) {
                deleteById(opt.get().getId());
            } else {
                entity.setId(counter);
                counter++;
            }
        } else {
            entity.setId(counter);
            counter++;
        }

        lists.add(entity);
        return entity;
    }

    @Override
    public <S extends TaskList> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<TaskList> findById(Long aLong) {
        for (TaskList list : lists) {
            if (list.getId().equals(aLong)) {
                return Optional.of(list);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        for (TaskList list : lists) {
            if (list.getId().equals(aLong)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends TaskList> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends TaskList> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<TaskList> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public TaskList getOne(Long aLong) {
        return null;
    }

    @Override
    public TaskList getById(Long aLong) {
        Optional<TaskList> opt = findById(aLong);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public <S extends TaskList> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends TaskList> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends TaskList> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends TaskList> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends TaskList> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends TaskList> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends TaskList, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<TaskCard> getTaskCardsId(Long id) {
        return null;
    }
}

