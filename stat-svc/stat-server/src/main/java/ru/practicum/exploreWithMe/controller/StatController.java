package ru.practicum.exploreWithMe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void saveEndpointHit(@RequestBody @Valid EndpointHitsDto endpointHitsDto) {
        log.info("Получен POST-запрос на сохранение статистики для эндпоинта: {}", endpointHitsDto);
        statService.saveEndpointHit(endpointHitsDto);
    }

    @GetMapping("/stats")
    public List<StatisticsDto> getStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                             @RequestParam(required = false, defaultValue = "0") String[] uris,
                                             @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.info("Получен GET-запрос на получение статистики с параметрами: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);
        return statService.getStatistics(start, end, uris, unique);
    }
}