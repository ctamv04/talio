package server.repositories;

import models.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collections;
import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    @Query("SELECT id FROM TaskCard WHERE taskList.id=?1 ORDER BY position")
    List<Long> getTaskCardsId(Long id);
}