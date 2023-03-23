package server.repositories;

import models.TaskCard;
import models.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collections;
import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    @Query("SELECT tc FROM TaskCard tc WHERE tc.taskList.id=?1 ORDER BY tc.position")
    List<TaskCard> getTaskCardsId(Long id);
}