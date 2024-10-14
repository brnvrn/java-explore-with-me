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
import ru.practicum.exploreWithMe.exception.EventException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
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
                PageRequest.of(from, size));
        log.info("Просмотр событий администратора с параметрами: users={}, states={}, " +
                        "categories={}, rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories,
                rangeStart, rangeEnd, from, size);
        return eventMapper.toEventDtoList(eventsPage.getContent());
    }

    @Transactional
    public EventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findEventById(eventId);

        updateEventFields(event, updateEventAdminRequest);
        handleAdminStateAction(event, updateEventAdminRequest);

        Event updatedEvent = eventRepository.save(event);
        log.info("Изменение собятия с id={} и входными парамертами :{} администратором",
                eventId, updateEventAdminRequest);
        return eventMapper.toEventDto(updatedEvent);
    }

    private LocalDateTime parseDate(String dateStr, LocalDateTime defaultValue) {
        if (dateStr == null || dateStr.isEmpty()) {
            return defaultValue;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(request.getCategory());
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }

    private void handleAdminStateAction(Event event, UpdateEventAdminRequest request) {
        if (request.getAdminStateAction() == null) return;

        switch (event.getState()) {
            case PENDING:
                handlePendingState(event, request);
                break;
            case PUBLISHED:
                handlePublishedState(event, request);
                break;
            case CANCELED:
                handleCanceledState(event, request);
                break;
            default:
                throw new EventException("Неизвестное состояние события");
        }
    }

    private void handlePendingState(Event event, UpdateEventAdminRequest request) {
        if (request.getAdminStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (request.getAdminStateAction().equals(AdminStateAction.REJECT_EVENT)) {
            event.setState(EventState.CANCELED);
        }
    }

    private void handlePublishedState(Event event, UpdateEventAdminRequest request) {
        if (request.getAdminStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            throw new EventException("Событие уже опубликовано");
        } else {
            throw new EventException("Нельзя отменить опубликованное событие");
        }
    }

    private void handleCanceledState(Event event, UpdateEventAdminRequest request) {
        if (request.getAdminStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            throw new EventException("Событие отменено");
        } else {
            throw new EventException("Событие уже отменено");
        }
    }
}