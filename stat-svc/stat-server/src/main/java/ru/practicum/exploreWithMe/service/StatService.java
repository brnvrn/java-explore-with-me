package ru.practicum.exploreWithMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.mapper.EndpointHitMapper;
import ru.practicum.exploreWithMe.model.EndpointHits;
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
    public EndpointHitsDto saveEndpointHit(EndpointHitsDto endpointHitsDto) {
        EndpointHits endpointHits = statRepository.save(endpointHitMapper.toEndpointHits(endpointHitsDto));
        log.info("Статистика успешно сохранена: {}", endpointHits);
        return endpointHitMapper.toEndpointHitsDto(endpointHits);
    }

    public List<StatisticsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Параметры 'start' и 'end' не могут быть null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Параметр 'start' должен быть раньше 'end'.");
        }
        if (unique) {
            if (uris.isEmpty()) {
                log.info("Получены уникальные IP статистики за период без фильтрации по URI");
                return endpointHitMapper.toStatisticsDtoList(statRepository.getUniqueIpStatisticsForPeriod(start, end));
            }
            log.info("Получены уникальные IP статистики за период для указанных URI");
            return endpointHitMapper.toStatisticsDtoList(statRepository.getUniqueIpStatisticsForPeriodAndUris(start, end, uris));
        }
        if (uris.isEmpty()) {
            log.info("Получена общая статистика за период без фильтрации по URI");
            return endpointHitMapper.toStatisticsDtoList(statRepository.getStatisticsForPeriod(start, end));
        }
        log.info("Получена общая статистика за период для указанных URI");
        return endpointHitMapper.toStatisticsDtoList(statRepository.getStatisticsForPeriodAndUris(start, end, uris));
    }

}
