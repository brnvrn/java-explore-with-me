package ru.practicum.exploreWithMe.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEventIdOrderByCreatedDesc(Long id, Pageable pageable);
}
