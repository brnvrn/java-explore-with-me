package ru.practicum.exploreWithMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.mapper.EndpointHitMapper;
import ru.practicum.exploreWithMe.model.EndpointHits;
import ru.practicum.exploreWithMe.model.Statistics;
import ru.practicum.exploreWithMe.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatService {
    private final EndpointHitMapper endpointHitMapper;
    private final StatRepository statRepository;

    @Transactional
    public void saveEndpointHit(EndpointHitsDto endpointHitsDto) {
        EndpointHits endpointHits = endpointHitMapper.toEndpointHits(endpointHitsDto);
        log.info("Статистика успешно сохранена: {}", endpointHits);
        statRepository.save(endpointHits);
    }

    public List<StatisticsDto> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Параметры start и end не могут быть null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Параметр start должен быть раньше end");
        }
        List<Statistics> statistics;
        if (uris[0].equals("0")) {
            if (unique) {
                statistics = statRepository.getUniqueIpStatisticsForPeriod(start, end);
            } else {
                statistics = statRepository.getStatisticsForPeriod(start, end);
            }
        } else {
            if (unique) {
                statistics = statRepository.getAllStatDataViewInUrisAndDistinctIp(uris, start, end);
            } else {
                statistics = statRepository.getUniqueIpStatisticsForPeriodAndUris(uris, start, end);
            }
        }
        return statistics.stream()
                .map(endpointHitMapper::toStatisticsDto)
                .toList();
    }
}
