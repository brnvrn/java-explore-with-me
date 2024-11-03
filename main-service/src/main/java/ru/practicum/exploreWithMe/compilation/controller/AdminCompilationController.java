package ru.practicum.exploreWithMe.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationRequest;
import ru.practicum.exploreWithMe.compilation.service.AdminCompilationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final AdminCompilationService adminCompilationService;

    @PostMapping
    public ResponseEntity<Object> addNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен POST-запрос на добавление новой подборки: {}", newCompilationDto);
        return ResponseEntity.status(201).body(adminCompilationService.addNewCompilation(newCompilationDto));
    }

    @PatchMapping("{compId}")
    public ResponseEntity<Object> updateCompilation(@PathVariable @NotNull Long compId,
                                                    @RequestBody @Valid UpdateCompilationRequest compilationRequest) {
        log.info("Получен PATCH-запрос на изменение подборки с id ={}", compId);
        return ResponseEntity.status(200).body(adminCompilationService.updateCompilation(compId, compilationRequest));
    }

    @DeleteMapping("{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable @NotNull Long compId) {
        log.info("Получен DELETE-запрос на удаление подборки с id ={}", compId);
        adminCompilationService.deleteCompilationByAdmin(compId);
        return ResponseEntity.status(204).build();
    }
}