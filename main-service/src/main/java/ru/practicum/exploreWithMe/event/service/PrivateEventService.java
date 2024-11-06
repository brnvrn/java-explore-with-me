package ru.practicum.exploreWithMe.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.event.dto.*;
import ru.practicum.exploreWithMe.event.mapper.EventMapper;
import ru.practicum.exploreWithMe.event.mapper.LocationMapper;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.model.Location;
import ru.practicum.exploreWithMe.event.model.PrivateStateAction;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.event.repository.LocationRepository;
import ru.practicum.exploreWithMe.exception.BadRequestException;
import ru.practicum.exploreWithMe.exception.EventException;
import ru.practicum.exploreWithMe.exception.NotFoundException;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventService {
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public EventFullDto addNewEventPrivate(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с таким айди не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new NotFoundException(
                "Категория с таким айди не найдена"));
        Location location = locationMapper.toLocation(newEventDto.getLocation());
        locationRepository.save(location);
        Event event = eventMapper.toEvent(newEventDto, location, category, user, LocalDateTime.now(),
                EventState.PENDING, 0, 0L);
        eventRepository.save(event);
        log.info("Добавление нового события с параметрами: {} от пользователя с id={} ", newEventDto, userId);
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Событие с таким айди не найдено"));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2)) ||
                event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Событие не может быть создано, если дата события менее чем " +
                    "через 2 часа или оно уже опубликовано");
        }
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не менее чем через 2 часа от текущего времени");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(PrivateStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequest.getStateAction().equals(PrivateStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else throw new IllegalArgumentException("Некорректный статус");
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() -> new NotFoundException(
                    "Категория с таким айди не найдена"));
            event.setEventCategory(category);
        }
        if (updateEventUserRequest.getLocation() != null) {
            NewLocationDto newLocationDto = updateEventUserRequest.getLocation();
            Location location = event.getEventLocation();
            locationMapper.setLocation(location, newLocationDto);
            locationRepository.save(location);
            event.setEventLocation(location);
        }
        eventMapper.setUpdateEventUserRequest(event, updateEventUserRequest);
        log.info("Изменение события с id={} от пользователя с id={} ", eventId, userId);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getUserEventsPrivate(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Просмотр всех событий пользователя с id={} и параметрами: from={}, size={}", userId, from, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с таким айди не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Событие с таким айди не найдено"));
        log.info("Просмотр события с id={} от пользователя с id={} на  ", eventId, userId);
        return eventMapper.toEventFullDto(event);
    }

    public List<RequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        log.info("Просмотр запросов на событие с id={} от пользователя с id={}", eventId, userId);
        return requestRepository.findByEventIdOrderByCreatedDesc(eventId).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatusPrivate(Long userId,
                                                                      Long eventId,
                                                                      EventRequestStatusUpdateRequest
                                                                              eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResult updateRequest = new EventRequestStatusUpdateResult()
                .setRejectedRequests(new ArrayList<>())
                .setConfirmedRequests(new ArrayList<>());
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Событие с таким айди не найдено"));
        List<Request> requestList = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());
        validateRequestStatus(requestList);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return updateRequest;
        }
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            requestList = requestList.stream()
                    .map(request -> request.setRequestStatus(RequestStatus.REJECTED))
                    .toList();
            requestRepository.saveAll(requestList);
            List<RequestDto> requestDtoList = requestList.stream()
                    .map(requestMapper::toRequestDto)
                    .toList();
            updateRequest.getRejectedRequests().addAll(requestDtoList);
            return updateRequest;
        } else {
            if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                throw new EventException("Лимит участников события превышен");
            }
            requestList = requestList.stream()
                    .map(request -> {
                        if (!event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                            request.setRequestStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            eventRepository.save(event);
                            updateRequest.getConfirmedRequests().add(requestMapper.toRequestDto(request));
                        } else {
                            request.setRequestStatus(RequestStatus.REJECTED);
                            updateRequest.getRejectedRequests().add(requestMapper.toRequestDto(request));
                        }
                        return request;
                    }).toList();
            requestRepository.saveAll(requestList);
            log.info("Изменение статуса запроса события с id={} от пользователя с id={} с параметрами: {}", eventId,
                    userId, eventRequestStatusUpdateRequest);
            return updateRequest;
        }
    }

    private void validateRequestStatus(List<Request> requests) {
        requests.forEach(request -> {
            if (!request.getRequestStatus().equals(RequestStatus.PENDING)) {
                throw new IllegalArgumentException("Запрос не находится на рассмотрении");
            }
        });
    }
}
