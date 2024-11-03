package ru.practicum.exploreWithMe.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.exploreWithMe.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventIdOrderByCreatedDesc(Long id);

    Request findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findByRequesterIdOrderByCreatedDesc(Long id);
}

