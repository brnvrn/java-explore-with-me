package ru.practicum.exploreWithMe.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.exploreWithMe.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM events AS e WHERE " +
            "(CAST(:users AS BIGINT) is null or e.initiator_id in (CAST(:users AS BIGINT))) " +
            "AND (CAST(:states AS TEXT) is null or e.event_state in (CAST(:states AS TEXT))) " +
            "AND (CAST(:categories AS BIGINT) is null or e.category_id in (CAST(:categories AS BIGINT))) " +
            "AND (CAST(:rangeStart AS TIMESTAMP) is null or e.event_date >= CAST(:rangeStart AS TIMESTAMP)) " +
            "AND (CAST(:rangeEnd AS TIMESTAMP) is null or e.event_date <= CAST(:rangeEnd  AS TIMESTAMP)) ",
            nativeQuery = true)
    List<Event> searchEventByAdmin(@Param("users") List<Long> users,
                                   @Param("states") List<String> states,
                                   @Param("categories") List<Long> categories,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);

    List<Event> findAllByInitiatorId(Long id, Pageable pageable);
    @Query(value = "SELECT * FROM events AS e " +
            "WHERE e.event_state = 'PUBLISHED' " +
            "AND (:text IS NULL OR e.annotation ILIKE CONCAT('%', :text, '%') " +
            "OR e.description ILIKE CONCAT('%', :text, '%')) " +
            "AND (:categories IS NULL OR e.category_id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd)",
            nativeQuery = true)
    List<Event> searchEventsPublic(@Param("text") String text,
                                   @Param("categories") List<Long> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);
    Boolean existsByEventCategoryId(Long catId);
}