package ru.practicum.exploreWithMe.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.event.dto.*;
import ru.practicum.exploreWithMe.event.service.PrivateEventService;
import ru.practicum.exploreWithMe.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
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
    public ResponseEntity<Object> addNewEventPrivate(@PathVariable Long userId,
                                                     @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получен POST-запрос на добавление события пользователя с id={} и параметрами: {}", userId,
                newEventDto);

        return ResponseEntity.status(201).body(privateEventService.addNewEventPrivate(userId, newEventDto));
    }

    @GetMapping("/{eventId}")
    public EventDto getEventByIdPrivate(@PathVariable Long userId,
                                        @PathVariable Long eventId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр события с id={} ", userId, eventId);
        return privateEventService.getEventByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEventByUser(@PathVariable @NotNull Long userId,
                                                    @PathVariable @NotNull Long eventId,
                                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return ResponseEntity.status(200).body(privateEventService.updateEventPrivate(userId, eventId, updateEventUserRequest));
    }


    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequestsPrivate(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long eventId) {
        log.info("Получен GET-запрос от пользователя с id={} на просмотр запросов на событие с id={} ", userId,
                eventId);
        return privateEventService.getEventRequestsPrivate(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable @NotNull Long userId,
                                                                   @PathVariable @NotNull Long eventId,
                                                                   @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return privateEventService.updateRequestsStatusPrivate(userId, eventId, request);
    }
}