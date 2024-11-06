package ru.practicum.exploreWithMe.category.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public static final String SIZE_10 = "10";
    public static final String SIZE_0 = "0";

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero
                                           @RequestParam(defaultValue = SIZE_0) Integer from,
                                           @Positive
                                           @RequestParam(defaultValue = SIZE_10) Integer size) {
        log.info("Получен GET-запрос на получение категорий с параметрами: from={}, size={}", from, size);
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable @NotNull Long catId) {
        log.info("Получен GET-запрос на получение категории с id ={}", catId);
        return categoryService.getCategoryById(catId);
    }
}