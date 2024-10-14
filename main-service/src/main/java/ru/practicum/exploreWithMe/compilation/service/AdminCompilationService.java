package ru.practicum.exploreWithMe.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.UpdateCompilationDto;
import ru.practicum.exploreWithMe.compilation.mapper.CompilationMapper;
import ru.practicum.exploreWithMe.compilation.model.Compilation;
import ru.practicum.exploreWithMe.compilation.repository.CompilationRepository;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.repository.EventRepository;

import java.util.ArrayList;
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
    public CompilationDto addNewCompilationByAdmin(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto, events));
        log.info("Добавление новой подборки: {}", newCompilationDto);

        return compilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public CompilationDto updateCompilationByAdmin(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findCompilationById(compId);
        if ((updateCompilationDto.getTitle()) != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        if ((updateCompilationDto.getPinned()) != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if ((updateCompilationDto.getEvents() != null) && !updateCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(updateCompilationDto.getEvents());
            compilation.setEvents(events);
        }
        Compilation updatedCompilation = compilationRepository.save(compilation);

        log.info("Обновление поборки с id ={}", compId);
        return compilationMapper.toCompilationDto(updatedCompilation);
    }

    @Transactional
    public void deleteCompilationByAdmin(Long compId) {
        compilationRepository.findCompilationById(compId);
        log.info("Удаление подборки с id ={}", compId);
        compilationRepository.deleteById(compId);
    }
}
