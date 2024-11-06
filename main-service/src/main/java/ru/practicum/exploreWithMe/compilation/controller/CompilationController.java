package ru.practicum.exploreWithMe.compilation.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;
    public static final String SIZE_10 = "10";
    public static final String SIZE_0 = "0";

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = SIZE_0) Integer from,
                                                @Positive @RequestParam(defaultValue = SIZE_10) Integer size) {
        log.info("Получение подборок с параметрами: pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable @NotNull Long compId) {
        log.info("Получение подборки с id ={}", compId);
        return compilationService.getCompilationById(compId);
    }
}