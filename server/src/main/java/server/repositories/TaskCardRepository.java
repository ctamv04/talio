package server.repositories;

import models.TaskCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCardRepository extends JpaRepository<TaskCard, Long> {
}
