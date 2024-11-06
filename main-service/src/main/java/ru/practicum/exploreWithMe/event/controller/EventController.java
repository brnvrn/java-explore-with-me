package ru.practicum.exploreWithMe.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.model.Sort;
import ru.practicum.exploreWithMe.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    public static final String SIZE_10 = "10";
    public static final String SIZE_0 = "0";

    @GetMapping
    public List<EventShortDto> searchEventsPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  @RequestParam(required = false) LocalDateTime rangeStart,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  @RequestParam(required = false) LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false) Sort sort,
                                                  @RequestParam(defaultValue = SIZE_0) @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = SIZE_10) @Positive Integer size,
                                                  @NonNull HttpServletRequest request) {
        log.info("Получен GET-запрос на получение событий с параметрами: text={}, categories={}, paid={}, " +
                        "rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.searchEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getPublicEventById(@Positive @NotNull
                                           @PathVariable(name = "id") Long eventId,
                                           @NonNull HttpServletRequest request) {
        log.info("Получен GET-запрос на получение событий с id={}", eventId);
        return eventService.getPublicEventById(eventId, request);
    }
}
