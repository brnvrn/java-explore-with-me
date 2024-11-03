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
            "WHERE e.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<Statistics> getUniqueIpStatisticsForPeriod(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHits e " +
            "WHERE e.uri IN ?1 " +
            "AND e.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<Statistics> getUniqueIpStatisticsForPeriodAndUris(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHits e " +
            "WHERE e.uri IN ?1 " +
            "AND e.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<Statistics> getAllStatDataViewInUrisAndDistinctIp(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHits e " +
            "WHERE e.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<Statistics> getStatisticsForPeriod(LocalDateTime start, LocalDateTime end);

}