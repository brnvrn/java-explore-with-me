package ru.practicum.exploreWithMe.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public RequestDto addNewRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с таким айди не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Событие с таким айди не найдено"));
        Request request = requestRepository.findByRequesterIdAndEventId(userId, eventId);

        validateRequestException(request, user, event);

        Request newParticipation = new Request()
                .setRequester(user)
                .setEvent(event)
                .setCreated(LocalDateTime.now())
                .setRequestStatus(RequestStatus.PENDING);
        if (event.getParticipantLimit() == 0) {
            newParticipation.setRequestStatus(RequestStatus.CONFIRMED);
        }
        if (!event.getRequestModeration() && event.getParticipantLimit() != 0) {
            newParticipation.setRequestStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        requestRepository.save(newParticipation);
        log.info("Добавление запроса на событие с id ={} от пользователя с id ={}", eventId, userId);
        return requestMapper.toRequestDto(newParticipation);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                "Запрос на событие с таким айди не найден"));
        Long eventId = request.getEvent().getId();
        if (request.getRequestStatus().equals(RequestStatus.PENDING)) {
            eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                    "Событие с таким айди не найдено"));
            request.setRequestStatus(RequestStatus.CANCELED);
            requestRepository.save(request);
        } else {
            throw new BadRequestException("Запрос на событие не находится на рассмотрении");
        }
        log.info("Отмена запроса с id ={} от пользователя с id ={}", requestId, userId);
        return requestMapper.toRequestDto(request);
    }

    public List<RequestDto> getAllUserRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с таким айди не найден"));
        log.info("Получение всех запросов на события от пользователя с id ={}", userId);
        return requestRepository.findByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    private void validateRequestException(Request request, User user, Event event) {
        if (request != null) {
            throw new IllegalArgumentException("Запрос на событие уже существует");
        }
        if (event.getInitiator().equals(user)) {
            throw new IllegalArgumentException("Вы являетесь собственником события");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Событие не опубликовано");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new EventException("Лимит участников события превышен");
        }
    }
}
