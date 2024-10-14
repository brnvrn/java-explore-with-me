package ru.practicum.exploreWithMe.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.compilation.repository.CompilationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Page<Compilation> page = compilationRepository.findByPinned(pinned, PageRequest.of(from, size));
        log.info("Получение подборок с параметрами: pinned={}, from={}, size={}", pinned, from, size);
        return compilationMapper.toCompilationDtoList(page.toList());
    }

    public CompilationDto getCompilationById(Long compId) {
        log.info("Получение подборки с id ={}", compId);
        return compilationMapper.toCompilationDto(compilationRepository.findCompilationById(compId));
    }
}
