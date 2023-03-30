package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import models.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {}