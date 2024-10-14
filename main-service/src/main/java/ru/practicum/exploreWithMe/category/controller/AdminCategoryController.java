package ru.practicum.exploreWithMe.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.service.AdminCategoryService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public CategoryDto addNewCategoryByAdmin(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен POST-запрос на добавление новой категории: {}", newCategoryDto);
        return adminCategoryService.addNewCategoryByAdmin(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategoryByAdmin(@PathVariable @Positive Long catId, @Valid @RequestBody NewCategoryDto
            newCategoryDto) {
        log.info("Получен PATCH-запрос на изменение категории с id ={}", catId);
        return adminCategoryService.updateCategoryByAdmin(catId, newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategoryByAdmin(@PathVariable @Positive Long catId) {
        log.info("Получен DELETE-запрос на удаление подборки с id ={}", catId);
        adminCategoryService.deleteCategoryByAdmin(catId);
    }
}