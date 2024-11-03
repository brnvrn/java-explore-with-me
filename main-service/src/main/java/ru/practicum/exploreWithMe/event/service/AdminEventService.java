package ru.practicum.exploreWithMe.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.category.repository.CategoryRepository;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.NewLocationDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventAdminRequest;
import ru.practicum.exploreWithMe.event.mapper.EventMapper;
import ru.practicum.exploreWithMe.event.mapper.LocationMapper;
import ru.practicum.exploreWithMe.event.model.AdminStateAction;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.model.Location;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.event.repository.LocationRepository;
import ru.practicum.exploreWithMe.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public List<EventFullDto> searchEventByAdmin(List<Long> users,
                                                 List<String> states,
                                                 List<Long> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size) {
        Pageable page = PageRequest.of(from / size, size);

        log.info("Просмотр событий администратора с параметрами: users={}, states={}, " +
                        "categories={}, rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories,
                rangeStart, rangeEnd, from, size);

        return eventRepository.searchEventByAdmin(users, states, categories, rangeStart, rangeEnd, page).stream()
                .toList().stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Событие с таким айди не надено"));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                !event.getState().equals(EventState.PENDING)) {
            throw new IllegalArgumentException("Недоступное событие");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                event.setViews(0L);
            } else if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Событие с таким айди не найдено"));
            event.setEventCategory(category);
        }
        if (updateEventAdminRequest.getLocation() != null) {
            NewLocationDto newLocationDto = updateEventAdminRequest.getLocation();
            Location location = event.getEventLocation();
            locationMapper.setLocation(location, newLocationDto);
            locationRepository.save(location);
            event.setEventLocation(location);
        }
        eventMapper.updateEventByAdmin(event, updateEventAdminRequest);
        eventRepository.save(event);
        return eventMapper.toEventFullDto(event);
    }
}
