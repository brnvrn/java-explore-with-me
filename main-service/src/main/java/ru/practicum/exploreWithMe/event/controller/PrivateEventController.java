package ru.practicum.exploreWithMe.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.event.dto.*;
import ru.practicum.exploreWithMe.event.service.PrivateEventService;
import ru.practicum.exploreWithMe.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getUserEventsPrivate(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос на просмотр всех событий пользователя с id={} и параметрами: from={}, size={}",
                userId, from, size);
        return privateEventService.getUserEventsPrivate(userId, from, size);
    }

    @PostMapping
    public EventDto addNewEventPrivate(@PathVariable Long userId,
                                       @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получен POST-запрос на добавление события пользователя с id={} и параметрами: {}", userId,
                newEventDto);

        return privateEventService.addNewEventPrivate(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventByIdPrivate(@PathVariable Long userId,
                                        @PathVariable Long eventId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр события с id={} ", userId, eventId);
        return privateEventService.getEventByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventPrivate(@PathVariable @Positive Long userId,
                                       @PathVariable @Positive Long eventId,
                                       @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Получен PATCH-запрос от пользователя с id={} на изменение события с id={} ", userId, eventId);
        return privateEventService.updateEventPrivate(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequestsPrivate(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long eventId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр запросов на событие с id={} ", userId,
                eventId);
        return privateEventService.getEventRequestsPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatusPrivate(@PathVariable @Positive Long userId,
                                                                      @PathVariable @Positive Long eventId,
                                                                      @Valid @RequestBody
                                                                      EventRequestStatusUpdateRequest request) {
        log.info("Получен PATCH-запрос от пользователя с id={} на изменение статуса запроса события с id={} ", userId,
                eventId);
        return privateEventService.updateRequestsStatusPrivate(userId, eventId, request);
    }
}