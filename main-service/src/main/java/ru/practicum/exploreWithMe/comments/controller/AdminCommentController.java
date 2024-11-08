package ru.practicum.exploreWithMe.comments.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.comments.dto.CommentFullDto;
import ru.practicum.exploreWithMe.comments.service.AdminCommentService;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/comments/{comId}")
@RequiredArgsConstructor
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @PatchMapping
    public CommentFullDto updateCommentStatus(@PathVariable @NotNull Long comId) {
        log.info("Получен PATCH-запрос на изменение статуса комментария с id ={}", comId);
        return adminCommentService.updateCommentStatus(comId);
    }

    @GetMapping
    public CommentFullDto getCommentById(@PathVariable @NotNull Long comId) {
        log.info("Получен GET-запрос на получения комментария с id ={}", comId);
        return adminCommentService.getCommentById(comId);
    }
}
