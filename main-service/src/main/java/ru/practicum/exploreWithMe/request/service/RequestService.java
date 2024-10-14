package ru.practicum.exploreWithMe.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.RequestException;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public RequestDto addNewRequest(Long userId, Long eventId) {
        User user = userRepository.findUserById(userId);
        Event event = eventRepository.findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestException("Событие не опубликовано");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestException("Запрос на событие уже существует");
        }

        if (event.getInitiator() == user) {
            throw new RequestException("Вы являетесь собственником события");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new RequestException("Лимит участников события превышен");
        }

        Request request = new Request();

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            if (event.getRequestModeration()) {
                request.setStatus(RequestStatus.PENDING);
            } else {
                request.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                eventRepository.save(event);
            }
        }
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        Request savedRequest = requestRepository.save(request);

        log.info("Добавление запроса на событие с id ={} от пользователя с id ={}", eventId, userId);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId);

        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);

        log.info("Отмена запроса с id ={} от пользователя с id ={}", requestId, userId);
        return requestMapper.toRequestDto(canceledRequest);
    }

    public List<RequestDto> getAllUserRequests(Long userId) {
        userRepository.findUserById(userId);
        log.info("Получение всех запросов на события от пользователя с id ={}", userId);
        return requestMapper.toRequestDtoList(requestRepository.findAllByRequesterId(userId));
    }
}