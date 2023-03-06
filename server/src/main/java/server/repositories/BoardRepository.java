package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import models.Board;

public interface BoardRepository extends JpaRepository<Board, String> {}
