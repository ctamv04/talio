package server.repositories;

import models.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import models.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {}
