package ru.practicum.exploreWithMe.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;
import ru.practicum.exploreWithMe.compilation.service.AdminCompilationService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final AdminCompilationService adminCompilationService;

    @PostMapping
    public CompilationDto addNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен POST-запрос на добавление новой подборки: {}", newCompilationDto);
        return adminCompilationService.addNewCompilationByAdmin(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable @Positive Long compId,
                                            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.info("Получен PATCH-запрос на изменение подборки с id ={}", compId);
        return adminCompilationService.updateCompilationByAdmin(compId, updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Получен DELETE-запрос на удаление подборки с id ={}", compId);
        adminCompilationService.deleteCompilationByAdmin(compId);
    }
}
