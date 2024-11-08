package ru.practicum.exploreWithMe.comments.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.comments.dto.CommentDto;
import ru.practicum.exploreWithMe.comments.dto.CommentFullDto;
import ru.practicum.exploreWithMe.comments.dto.UpdateCommentDto;
import ru.practicum.exploreWithMe.comments.service.PrivateCommentService;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final PrivateCommentService privateCommentService;

    @PostMapping
    ResponseEntity<Object> addNewComment(@PathVariable @NotNull Long userId,
                                         @RequestParam @NotNull Long eventId,
                                         @RequestBody @Valid CommentDto commentDto) {
        log.info("Получен POST-запрос на нового комментария от пользователя с id={} на событие с id={}",
                userId, eventId);
        return ResponseEntity.status(201).body(privateCommentService.addNewComment(userId, eventId, commentDto));
    }

    @PatchMapping("/{comId}")
    CommentFullDto updateComment(@PathVariable @NotNull Long userId,
                                 @PathVariable @NotNull Long comId,
                                 @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        log.info("Получен PATCH-запрос на изменение комментария с id ={} от пользователя с id ={}", comId, userId);
        return privateCommentService.updateComment(userId, comId, updateCommentDto);
    }

    @DeleteMapping("/{comId}")
    ResponseEntity<Object> deleteComment(@PathVariable @NotNull Long userId, @PathVariable @NotNull Long comId) {
        log.info("Получен DELETE-запрос на удаление комментария с id ={} от пользователя с id ={}", comId, userId);
        privateCommentService.deleteComment(userId, comId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{comId}")
    CommentFullDto getComment(@PathVariable @NotNull Long userId,
                              @PathVariable @NotNull Long comId) {
        log.info("Получен GET-запрос на получение комментария с id ={} от пользователя с id ={}", comId, userId);
        return privateCommentService.getComment(userId, comId);
    }
}
