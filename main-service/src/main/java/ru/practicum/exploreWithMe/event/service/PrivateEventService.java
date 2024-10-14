package ru.practicum.exploreWithMe.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.event.dto.*;
import ru.practicum.exploreWithMe.event.mapper.EventMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventRequestStatus;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.EventException;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.exception.NotInitiatorException;
import ru.practicum.exploreWithMe.exception.NotModerationException;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.mapper.RequestMapper;
import ru.practicum.exploreWithMe.request.model.Request;
import ru.practicum.exploreWithMe.request.model.RequestStatus;
import ru.practicum.exploreWithMe.request.repository.RequestRepository;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public EventDto addNewEventPrivate(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findUserById(userId);
        Category category = categoryRepository.findCategoryById(newEventDto.getCategory());
        Event event = eventMapper.toEvent(newEventDto, category, user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState((EventState.PENDING));
        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        eventDto.setViews(0L);

        log.info("Добавление события пользователя с id={} и параметрами: {}", userId, newEventDto);
        return eventDto;
    }

    public EventDto getEventByIdPrivate(Long userId, Long eventId) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotInitiatorException("Просмотр возможен только инициатору события");
        }
        log.info("Просмотр события с id={} от пользователя с id={} на  ", eventId, userId);
        return eventMapper.toEventDto(event);
    }

    @Transactional
    public EventDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        validateUserAndEvent(userId, eventId);
        Event event = eventRepository.findEventById(eventId);

        checkEventState(event);
        updateEventFields(event, updateEventUserRequest);
        handlePrivateStateAction(event, updateEventUserRequest);

        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        eventDto.setViews(0L);
        log.info("Изменение события с id={} от пользователя с id={}  ", eventId, userId);
        return eventDto;
    }

    public List<EventShortDto> getUserEventsPrivate(Long userId, int from, int size) {
        userRepository.findUserById(userId);
        Page<Event> page = eventRepository.findByInitiatorId(userId, PageRequest.of(from, size));

        log.info("Просмотр всех событий пользователя с id={} и параметрами: from={}, size={}", userId, from, size);
        return eventMapper.toEventShortDtoList(page.toList());
    }

    public List<RequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException("Вы не являетесь инициатором этого события.");
        }
        log.info("Просмотр запросов на событие с id={} от пользователя с id={}", eventId, userId);
        return requestMapper.toRequestDtoList(requestRepository.findAllByEventId(eventId));

    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatusPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        validateUserAndEvent(userId, eventId);
        Event event = eventRepository.findEventById(eventId);

        checkModerationRequirements(event);
        checkParticipantLimit(event);

        List<Request> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());
        validateRequestsExistence(requests, statusUpdateRequest.getRequestIds());

        log.info("Изменение статуса запроса события с id={} от пользователя с id={} на  ", eventId, userId);
        return processRequests(event, requests, statusUpdateRequest);
    }

    private void checkEventState(Event event) {
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventException("Опубликованное событие нельзя обновить");
        }
    }

    private void updateEventFields(Event event, UpdateEventUserRequest request) {
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
    }

    private void handlePrivateStateAction(Event event, UpdateEventUserRequest request) {
        if (request.getPrivateStateAction() == null) return;

        switch (request.getPrivateStateAction()) {
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new NotModerationException("Неправильное состояние");
        }
    }

    private void validateUserAndEvent(Long userId, Long eventId) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Вы не инициатор события");
        }
    }

    private void checkModerationRequirements(Event event) {
        if (!event.getRequestModeration()) {
            throw new NotModerationException("Модерация запросов не требуется для этого события");
        }
    }

    private void checkParticipantLimit(Event event) {
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new EventException("Лимит участников события превышен");
        }
    }

    private void validateRequestsExistence(List<Request> requests, List<Long> requestIds) {
        if (requests.size() != requestIds.size()) {
            throw new NotFoundException("Запрос не найден");
        }
    }

    private EventRequestStatusUpdateResult processRequests(Event event, List<Request> requests, EventRequestStatusUpdateRequest statusUpdateRequest) {
        EventRequestStatusUpdateResult resultDto = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());

        for (Request request : requests) {
            if (statusUpdateRequest.getStatus().equals(EventRequestStatus.REJECTED)) {
                handleRejectedRequest(request, resultDto);
            } else if (statusUpdateRequest.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                handleConfirmedRequest(event, request, resultDto);
            }
        }

        requestRepository.saveAll(requests);
        eventRepository.save(event);

        return resultDto;
    }

    private void handleRejectedRequest(Request request, EventRequestStatusUpdateResult resultDto) {
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new EventException("Подтвержденный запрос нельзя изменить");
        }

        request.setStatus(RequestStatus.REJECTED);
        resultDto.getRejectedRequests().add(requestMapper.toRequestDto(request));
    }

    private void handleConfirmedRequest(Event event, Request request, EventRequestStatusUpdateResult resultDto) {
        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            resultDto.getConfirmedRequests().add(requestMapper.toRequestDto(request));
        } else {
            request.setStatus(RequestStatus.REJECTED);
            resultDto.getRejectedRequests().add(requestMapper.toRequestDto(request));
        }
    }
}
