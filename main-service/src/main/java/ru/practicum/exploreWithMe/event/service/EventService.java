package ru.practicum.exploreWithMe.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.client.StatClient;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.mapper.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.model.Sort;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.BadRequestException;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.exception.SortException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatClient statsClient;


    public List<EventShortDto> searchEventsPublic(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  Sort sort,
                                                  Integer from,
                                                  Integer size,
                                                  HttpServletRequest servletRequest) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Дата окончания поиска не может быть раньше даты начала");
        }
        sendHttpRequest(servletRequest);
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(10);
        Pageable page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.searchEventsPublic(text, categories, paid, start, end, page);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() > event.getConfirmedRequests())
                    .toList();
        }
        if (sort != null) {
            validateSort(sort);
            if (sort.equals(Sort.EVENT_DATE)) {
                events.sort(Comparator.comparing(Event::getEventDate));
            } else {
                events.sort(Comparator.comparing(Event::getViews));
            }
        }
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest servletRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Событие с таким айди не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие не было опубликовано");
        }
        sendHttpRequest(servletRequest);
        Long views = getUniqueEventViews(servletRequest, event);
        event.setViews(views);
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public void sendHttpRequest(HttpServletRequest request) {
        EndpointHitsDto endpointHitsDto = new EndpointHitsDto()
                .setApp("main-service")
                .setIp(request.getRemoteAddr())
                .setUri(request.getRequestURI())
                .setTimestamp(LocalDateTime.now());
        statsClient.sendHttpRequest(endpointHitsDto);
    }

    public Long getUniqueEventViews(HttpServletRequest request, Event event) {
        LocalDateTime start = event.getPublishedOn().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        String[] uri = new String[]{request.getRequestURI()};
        List<StatisticsDto> statResponseDtoList = statsClient.getStats(start, end, uri, true);
        return statResponseDtoList.stream()
                .filter(statisticsDto -> statisticsDto.getApp().equals("main-service")
                        && statisticsDto.getUri().equals(request.getRequestURI()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Данные не найдены"))
                .getHits();
    }

    public void validateSort(Sort sort) {
        if (!Arrays.asList(Sort.values()).contains(sort)) {
            throw new SortException("Такого параметра сортировки не существует");
        }
    }
}
