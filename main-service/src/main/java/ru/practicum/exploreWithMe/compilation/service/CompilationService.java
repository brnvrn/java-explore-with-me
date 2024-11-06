package ru.practicum.exploreWithMe.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.compilation.repository.CompilationRepository;
import ru.practicum.exploreWithMe.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            log.info("Получение подборок с параметрами: from={}, size={}", from, size);
            return compilationRepository.findAll(pageable).stream()
                    .map(compilationMapper::toCompilationDto)
                    .toList();
        }
        log.info("Получение подборок с параметрами: pinned={}, from={}, size={}", pinned, from, size);
        return compilationRepository.findByPinned(pinned, pageable).stream()
                .map(compilationMapper::toCompilationDto)
                .toList();
    }

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка с таким айди не существует"));
        log.info("Получение подборки с id ={}", compId);
        return compilationMapper.toCompilationDto(compilation);
    }
}
