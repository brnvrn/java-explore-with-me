package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.exploreWithMe.model.EndpointHits;
import ru.practicum.exploreWithMe.model.Statistics;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHits, Long> {
    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHits e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri")
    List<Statistics> getUniqueIpStatisticsForPeriod(LocalDateTime start, LocalDateTime end);


    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHits e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri")
    List<Statistics> getUniqueIpStatisticsForPeriodAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(e)) " +
            "FROM EndpointHits e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri")
    List<Statistics> getStatisticsForPeriodAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(e)) " +
            "FROM EndpointHits e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri")
    List<Statistics> getStatisticsForPeriod(LocalDateTime start, LocalDateTime end);
}