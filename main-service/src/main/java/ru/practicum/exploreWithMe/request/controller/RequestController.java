package ru.practicum.exploreWithMe.request.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@PathVariable @NotNull Long userId,
                                                @RequestParam @NotNull Long eventId) {
        log.info("Получен POST-запрос на добавление запроса на событие с id ={} от пользователя с id ={}", eventId, userId);
        return ResponseEntity.status(201).body(requestService.addNewRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@PathVariable @NotNull Long userId,
                                                @PathVariable @NotNull Long requestId) {
        log.info("Получен PATCH-запрос на отмену запроса с id ={} от пользователя с id ={}", requestId, userId);
        return ResponseEntity.status(200).body(requestService.cancelRequest(userId, requestId));
    }

    @GetMapping
    public List<RequestDto> getAllUserRequests(@PathVariable @NotNull Long userId) {
        log.info("Получен GET-запрос на получение всех запросов на события от пользователя с id ={}", userId);
        return requestService.getAllUserRequests(userId);
    }
}