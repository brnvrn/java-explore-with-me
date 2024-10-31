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
import ru.practicum.exploreWithMe.exception.*;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.mapper.RequestMapper;
import ru.practicum.exploreWithMe.request.model.Request;
import ru.practicum.exploreWithMe.request.model.RequestStatus;
import ru.practicum.exploreWithMe.request.repository.RequestRepository;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        event.setState(EventState.PENDING);
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());

        Event savedEvent = eventRepository.save(event);

        EventDto eventDto = eventMapper.toEventDto(savedEvent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = savedEvent.getEventDate().format(formatter);
        eventDto.setViews(0L);
        eventDto.setLocation(new LocationDto(event.getLat(), event.getLon()));
        eventDto.setState(EventState.PENDING);
        eventDto.setEventDate(formattedDate);
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

    public EventDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя обновить опубликованное событие");
        }
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Вы не являетесь инициатором данного события");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findCategoryById(updateEventUserRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLat(updateEventUserRequest.getLocation().getLat());
            event.setLon(updateEventUserRequest.getLocation().getLon());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new NotModerationException("Некорректное значение.");
            }
        }
        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(event));
        eventDto.setViews(0L);
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
        if (event.getParticipantLimit() != null && event.getConfirmedRequests() >= event.getParticipantLimit()) {
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
