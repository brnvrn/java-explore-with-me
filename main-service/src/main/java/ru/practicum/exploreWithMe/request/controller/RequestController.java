package ru.practicum.exploreWithMe.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@PathVariable @Positive Long userId, @RequestParam Long eventId) {
        log.info("Получен POST-запрос на добавление запроса на событие с id ={} от пользователя с id ={}", eventId, userId);
        return ResponseEntity.status(201).body(requestService.addNewRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive Long userId, @PathVariable @Positive Long requestId) {
        log.info("Получен PATCH-запрос на отмену запроса с id ={} от пользователя с id ={}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getAllUserRequests(@PathVariable @Positive Long userId) {
        log.info("Получен GET-запрос на получение всех запросов на события от пользователя с id ={}", userId);
        return requestService.getAllUserRequests(userId);
    }
}