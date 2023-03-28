package server.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import models.TaskList;
import org.springframework.data.jpa.repository.Query;
import models.TaskCard;


public interface TaskListRepository extends JpaRepository<TaskList,Long> {
    @Query ("SELECT tc FROM TaskCard tc WHERE tc.taskList.id=?1 ORDER BY tc.position")
    List<TaskCard> getTaskCardsId(Long id);
}