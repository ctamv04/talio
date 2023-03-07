package server.repositories;

import models.Tasklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TasklistRepository extends JpaRepository<Tasklist, Long> {}