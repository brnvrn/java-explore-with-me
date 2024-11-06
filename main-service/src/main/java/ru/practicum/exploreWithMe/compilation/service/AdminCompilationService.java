package ru.practicum.exploreWithMe.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationRequest;
import ru.practicum.exploreWithMe.compilation.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.compilation.repository.CompilationRepository;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }
        compilationRepository.save(compilation);
        log.info("Добавление новой подборки: {}", newCompilationDto);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                "Подборка с таким айди не существует"));
        if (compilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(compilationRequest.getEvents());
            compilation.setEvents(events);
        }
        compilationMapper.setUpdateCompilationRequest(compilation, compilationRequest);
        compilationRepository.save(compilation);
        log.info("Обновление поборки с id ={}", compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public void deleteCompilationByAdmin(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                "Подборка с таким айди не существует"));
        log.info("Удаление подборки с id ={}", compId);
        compilationRepository.deleteById(compId);
    }
}
