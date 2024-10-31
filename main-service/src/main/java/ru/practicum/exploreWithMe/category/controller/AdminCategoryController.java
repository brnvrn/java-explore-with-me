package ru.practicum.exploreWithMe.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.exploreWithMe.category.service.AdminCategoryService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<Object> addNewCategoryByAdmin(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен POST-запрос на добавление новой категории: {}", newCategoryDto);
        return ResponseEntity.status(201).body(adminCategoryService.addNewCategoryByAdmin(newCategoryDto));
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> updateCategoryByAdmin(@PathVariable @Positive Long catId,
                                                        @Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен PATCH-запрос на изменение категории с id ={}", catId);
        return ResponseEntity.status(200).body(adminCategoryService.updateCategoryByAdmin(catId, newCategoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategoryByAdmin(@PathVariable @Positive Long catId) {
        log.info("Получен DELETE-запрос на удаление подборки с id ={}", catId);
        adminCategoryService.deleteCategoryByAdmin(catId);
        return ResponseEntity.status(204).build();
    }
}