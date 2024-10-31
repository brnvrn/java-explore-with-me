package ru.practicum.exploreWithMe.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.client.StatClient;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.event.dto.EventDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.mapper.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.model.Sort;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.JsonException;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.exception.NotModerationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatClient statClient;

    public List<EventShortDto> searchEventsPublic(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size,
                                                  HttpServletRequest request) {
        LocalDateTime startTime = parseDate(rangeStart, LocalDateTime.now());
        LocalDateTime endTime = parseDate(rangeEnd, LocalDateTime.now().plusYears(1000));

        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (endTime.isBefore(startTime)) {
            throw new NotModerationException("Дата окончания поиска не может быть раньше даты начала");
        }
        Sort sortValue;
        if (sort != null) {
            if ("EVENT_DATE".equalsIgnoreCase(sort)) {
                sortValue = Sort.EVENT_DATE;
            } else if ("VIEWS".equalsIgnoreCase(sort)) {
                sortValue = Sort.VIEWS;
            } else {
                throw new NotModerationException("Неправильное значение сортировки");
            }
        } else {
            sortValue = Sort.VIEWS;
        }

        Page<Event> page;
        if (onlyAvailable) {
            page = eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, startTime, endTime,
                    PageRequest.of(from, size));
        } else {
            page = eventRepository.findAllByPublicFilters(text, categories, paid, startTime, endTime,
                    PageRequest.of(from, size));
        }

        List<Event> events = page.getContent();
        List<String> eventUris = new ArrayList<>();
        for (Event event : events) {
            eventUris.add(request.getRequestURI() + "/" + event.getId());
        }

        List<Event> sortEventList = new ArrayList<>(events);
        sortEventList.sort(Comparator.comparing(Event::getPublishedOn));

        List<StatisticsDto> statisticsDtoList = getStatistics(startTime, endTime, eventUris);

        Map<String, Long> statistics = new HashMap<>();
        for (StatisticsDto stat : statisticsDtoList) {
            statistics.put(stat.getUri(), stat.getHits());
        }

        addEndpointHit(request);

        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : events) {
            EventShortDto dto = eventMapper.toEventShortDto(event);
            String eventUri = request.getRequestURI() + "/" + event.getId();
            dto.setViews(statistics.getOrDefault(eventUri, 0L));
            eventShortDtoList.add(dto);
        }

        if (sortValue.equals(Sort.EVENT_DATE)) {
            eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
        } else {
            eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
        }

        log.info("Получение событий с параметрами: text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        return eventShortDtoList;
    }

    public EventDto getEventByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findEventById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " не найдено");
        }
        addEndpointHit(request);

        List<StatisticsDto> statisticsDtoList = getStatistics(event.getPublishedOn().minusSeconds(1),
                LocalDateTime.now(), List.of(request.getRequestURI()));

        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        if (!statisticsDtoList.isEmpty()) {
            eventDto.setViews(statisticsDtoList.get(0).getHits());
        } else {
            eventDto.setViews(0L);
        }

        log.info("Получение событий с id={}", eventId);
        return eventDto;
    }

    private LocalDateTime parseDate(String dateStr, LocalDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private List<StatisticsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris) {
        ResponseEntity<Object> uri = statClient.getStats(start, end, uris, true);
        List<StatisticsDto> statisticsDtoList = new ArrayList<>();
        if (uri.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(uri.getBody());
                statisticsDtoList = objectMapper.readValue(json, new TypeReference<List<StatisticsDto>>() {
                });
            } catch (JsonProcessingException e) {
                throw new JsonException("Ошибка обработки JSON");
            }
        }
        return statisticsDtoList;
    }

    private void addEndpointHit(HttpServletRequest request) {
        statClient.sendHttpRequest(new EndpointHitsDto(
                0L,
                "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }
}