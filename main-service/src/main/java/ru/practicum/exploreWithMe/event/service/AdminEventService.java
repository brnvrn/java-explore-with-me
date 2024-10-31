package ru.practicum.exploreWithMe.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.event.dto.EventDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;
import ru.practicum.exploreWithMe.event.mapper.EventMapper;
import ru.practicum.exploreWithMe.event.model.AdminStateAction;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.ConflictException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    public List<EventDto> searchEventByAdmin(List<Long> users,
                                             List<String> states,
                                             List<Long> categories,
                                             String rangeStart,
                                             String rangeEnd,
                                             int from,
                                             int size) {

        LocalDateTime startTime = parseDate(rangeStart, LocalDateTime.now());
        LocalDateTime endTime = parseDate(rangeEnd, LocalDateTime.now().plusYears(1000));

        users = (users != null && !users.isEmpty()) ? users : null;
        states = (states != null && !states.isEmpty()) ? states : null;
        categories = (categories != null && !categories.isEmpty()) ? categories : null;

        Page<Event> eventsPage = eventRepository.searchEvents(users, states, categories, startTime, endTime,
                PageRequest.of(from / size, size));
        log.info("Просмотр событий администратора с параметрами: users={}, states={}, " +
                        "categories={}, rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories,
                rangeStart, rangeEnd, from, size);
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event : eventsPage.getContent()) {
            eventDtos.add(EventMapper.toEventDto(event));
        }

        return eventDtos;
    }

    @Transactional
    public EventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findEventById(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLat(updateEventAdminRequest.getLocation().getLat());
            event.setLon(updateEventAdminRequest.getLocation().getLon());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING)) {
                if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                    event.setState(EventState.CANCELED);
                }
            } else if (event.getState().equals(EventState.PUBLISHED)) {
                if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                    throw new ConflictException("Событие уже опубликовано и не может быть опубликовано повторно.");
                } else {
                    throw new ConflictException("Событие уже опубликовано и не может быть отменено.");
                }
            } else {
                if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                    throw new ConflictException("Событие отменено и не может быть опубликовано.");
                } else {
                    throw new ConflictException("Событие уже отменено и не может быть отменено повторно.");
                }
            }
        }
        Event updatedEvent = eventRepository.save(event);
        return EventMapper.toEventDto(updatedEvent);
    }

    private LocalDateTime parseDate(String dateStr, LocalDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}