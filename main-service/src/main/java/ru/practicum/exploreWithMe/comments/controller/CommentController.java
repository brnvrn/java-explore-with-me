package ru.practicum.exploreWithMe.comments.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.comments.dto.CommentShortDto;
import ru.practicum.exploreWithMe.comments.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    public static final String SIZE_10 = "10";
    public static final String SIZE_0 = "0";

    @GetMapping("/{eventId}")
    public List<CommentShortDto> getCommentsByEventId(@PathVariable @NotNull Long eventId,
                                                      @RequestParam(defaultValue = "NEWEST") String sort,
                                                      @RequestParam(defaultValue = SIZE_0) Integer from,
                                                      @RequestParam(defaultValue = SIZE_10) Integer size) {
        log.info("Получен GET-запрос на получение комментариев на событие с id={} с параметрами: from={}, size={}",
                eventId, from, size);
        return commentService.getCommentsByEventId(eventId, sort, from, size);
    }
}
