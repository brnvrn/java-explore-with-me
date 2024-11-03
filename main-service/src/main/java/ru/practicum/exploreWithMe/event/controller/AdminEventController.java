package ru.practicum.exploreWithMe.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;
import ru.practicum.exploreWithMe.event.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventService adminEventService;
    public static final String SIZE_10 = "10";
    public static final String SIZE_0 = "0";

    @GetMapping
    List<EventFullDto> searchEventByAdmin(@RequestParam(required = false) List<Long> users,
                                          @RequestParam(required = false) List<String> states,
                                          @RequestParam(required = false) List<Long> categories,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                          @RequestParam(required = false) LocalDateTime rangeStart,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                          @RequestParam(required = false) LocalDateTime rangeEnd,
                                          @PositiveOrZero
                                          @RequestParam(defaultValue = SIZE_0) Integer from,
                                          @Positive
                                          @RequestParam(defaultValue = SIZE_10) Integer size) {
        log.info("Получен GET-запрос на просмотр событий администратора с параметрами: users={}, states={}, " +
                        "categories={}, rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories,
                rangeStart, rangeEnd, from, size);
        return adminEventService.searchEventByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @NotNull Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Получен PATCH-запрос на изменение собятия с id={} и входными парамертами :{} администратором",
                eventId, updateEventAdminRequest);
        return adminEventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}