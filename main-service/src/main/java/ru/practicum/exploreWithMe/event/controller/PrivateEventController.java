package ru.practicum.exploreWithMe.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.event.dto.*;
import ru.practicum.exploreWithMe.event.service.PrivateEventService;
import ru.practicum.exploreWithMe.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventService privateEventService;
    public static final String SIZE_10 = "10";
    public static final String SIZE_0 = "0";

    @PostMapping
    public ResponseEntity<Object> addNewEventPrivate(@PathVariable @NotNull Long userId,
                                                     @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен POST-запрос на добавление события пользователя с id={} и параметрами: {}", userId,
                newEventDto);
        return ResponseEntity.status(201).body(privateEventService.addNewEventPrivate(userId, newEventDto));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEventByUser(@PathVariable @NotNull Long userId,
                                                    @PathVariable @NotNull Long eventId,
                                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Получен PATCH-запрос на изменение события с id={} пользователем с id={} и параметрами: {}", eventId,
                userId, updateEventUserRequest);
        return ResponseEntity.status(200).body(privateEventService.updateEventPrivate(userId, eventId,
                updateEventUserRequest));
    }

    @GetMapping
    public List<EventShortDto> getUserEventsPrivate(@PathVariable Long userId,
                                                    @PositiveOrZero
                                                    @RequestParam(defaultValue = SIZE_0) Integer from,
                                                    @Positive
                                                    @RequestParam(defaultValue = SIZE_10) Integer size) {
        log.info("Получен GET-запрос на просмотр всех событий пользователя с id={} и параметрами: from={}, size={}",
                userId, from, size);
        return privateEventService.getUserEventsPrivate(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdPrivate(@PathVariable @NotNull Long userId,
                                            @PathVariable @NotNull Long eventId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр события с id={} ", userId, eventId);
        return privateEventService.getEventByIdPrivate(userId, eventId);
    }

    @GetMapping("{eventId}/requests")
    public List<RequestDto> getEventRequestsPrivate(@PathVariable @NotNull Long userId,
                                                    @PathVariable @NotNull Long eventId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр запросов на событие с id={} ", userId,
                eventId);
        return privateEventService.getEventRequestsPrivate(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public ResponseEntity<Object> updateRequestsStatusPrivate(@PathVariable @NotNull Long userId,
                                                              @PathVariable @NotNull Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest
                                                                      eventRequestStatusUpdateRequest) {
        log.info("Получен PATCH-запрос на изменение статуса запроса события с id={} пользователем с id={} " +
                "и параметрами: {}", eventId, userId, eventRequestStatusUpdateRequest);
        return ResponseEntity.status(200).body(privateEventService.updateRequestsStatusPrivate(userId, eventId,
                eventRequestStatusUpdateRequest));
    }
}
