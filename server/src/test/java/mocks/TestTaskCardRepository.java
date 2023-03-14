package mocks;

import models.TaskCard;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.repositories.TaskCardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("all")
public class TestTaskCardRepository implements TaskCardRepository {
    private long counter = 1;
    public final List<TaskCard> taskCards = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    public List<String> getCalledMethods() {
        return calledMethods;
    }

    @Override
    public List<TaskCard> findAll() {
        call("findAll");
        return taskCards;
    }

    public List<TaskCard> findAll(Sort sort) {
        return null;
    }

    public Page<TaskCard> findAll(Pageable pageable) {
        return null;
    }

    public List<TaskCard> findAllById(Iterable<Long> longs) {
        return null;
    }

    public long count() {
        return 0;
    }

    public void deleteById(Long aLong) {
        Optional<TaskCard> opt = findById(aLong);
        opt.ifPresent(taskCards::remove);
    }

    public void delete(TaskCard entity) {

    }

    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    public void deleteAll(Iterable<? extends TaskCard> entities) {

    }

    public void deleteAll() {

    }

    public <S extends TaskCard> S save(S entity) {
        call("save");
        if (entity.getId() != null) {
            Optional<TaskCard> opt = findById(entity.getId());
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

        taskCards.add(entity);
        return entity;
    }

    public <S extends TaskCard> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    public Optional<TaskCard> findById(Long aLong) {
        for (TaskCard taskCard : taskCards) {
            if (taskCard.getId().equals(aLong)) {
                return Optional.of(taskCard);
            }
        }
        return Optional.empty();
    }

    public boolean existsById(Long aLong) {
        for (TaskCard taskCard : taskCards) {
            if (taskCard.getId().equals(aLong)) {
                return true;
            }
        }
        return false;
    }

    public void flush() {

    }

    public <S extends TaskCard> S saveAndFlush(S entity) {
        return null;
    }

    public <S extends TaskCard> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    public void deleteAllInBatch(Iterable<TaskCard> entities) {

    }

    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    public void deleteAllInBatch() {

    }

    public TaskCard getOne(Long aLong) {
        return null;
    }

    public TaskCard getById(Long aLong) {
        Optional<TaskCard> opt = findById(aLong);
        if (!opt.isEmpty()) {
            return opt.get();
        }
        return null;
    }

    public <S extends TaskCard> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    public <S extends TaskCard> List<S> findAll(Example<S> example) {
        return null;
    }

    public <S extends TaskCard> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    public <S extends TaskCard> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    public <S extends TaskCard> long count(Example<S> example) {
        return 0;
    }

    public <S extends TaskCard> boolean exists(Example<S> example) {
        return false;
    }

    public <S extends TaskCard, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
