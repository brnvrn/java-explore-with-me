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
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatService {
    private final StatRepository statRepository;

    @Transactional
    public EndpointHitsDto saveEndpointHit(EndpointHitsDto endpointHitsDto) {
        EndpointHits endpointHits = statRepository.save(EndpointHitMapper.toEndpointHits(endpointHitsDto));
        log.info("Статистика успешно сохранена: {}", endpointHits);
        return EndpointHitMapper.toEndpointHitsDto(endpointHits);
    }

    public List<StatisticsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Параметры 'start' и 'end' не могут быть null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Параметр 'start' должен быть раньше 'end'.");
        }
        if (unique) {
            if (Objects.isNull(uris) || uris.isEmpty()) {
                log.info("Получены уникальные IP статистики за период без фильтрации по URI");
                return EndpointHitMapper.toStatisticsDtoList(statRepository.getUniqueIpStatisticsForPeriod(start, end));
            }
            log.info("Получены уникальные IP статистики за период для указанных URI");
            return EndpointHitMapper.toStatisticsDtoList(statRepository.getUniqueIpStatisticsForPeriodAndUris(start, end, uris));
        }
        if (Objects.isNull(uris) || uris.isEmpty()) {
            log.info("Получена общая статистика за период без фильтрации по URI");
            return EndpointHitMapper.toStatisticsDtoList(statRepository.getStatisticsForPeriod(start, end));
        }
        log.info("Получена общая статистика за период для указанных URI");
        return EndpointHitMapper.toStatisticsDtoList(statRepository.getStatisticsForPeriodAndUris(start, end, uris));
    }
}
