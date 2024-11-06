package ru.practicum.exploreWithMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.exploreWithMe.model.EndpointHits;
import ru.practicum.exploreWithMe.model.Statistics;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHits, Long> {
    @Query("select new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, count(e.ip)) from EndpointHits e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 group by e.app, e.uri order by count(e.ip) DESC")
    List<Statistics> getAllStatDataView(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, count(distinct e.ip)) from EndpointHits e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 group by e.app, e.uri order by count(distinct e.ip) DESC")
    List<Statistics> getAllStatDataViewWithDistinctIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, count(e.ip)) from EndpointHits e " +
            "where e.uri in ?1 and e.timestamp >= ?2 and e.timestamp <= ?3 group by e.app, e.uri order by count(e.ip) DESC")
    List<Statistics> getAllStatDataViewInUris(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.exploreWithMe.model.Statistics(e.app, e.uri, count(distinct e.ip)) from EndpointHits e " +
            "where e.uri in ?1 and e.timestamp >= ?2 and e.timestamp <= ?3 group by e.app, e.uri order by count(distinct e.ip) DESC")
    List<Statistics> getAllStatDataViewInUrisAndDistinctIp(String[] uris, LocalDateTime start, LocalDateTime end);
}