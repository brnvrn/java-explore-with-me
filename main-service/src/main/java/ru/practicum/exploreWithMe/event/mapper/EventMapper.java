package ru.practicum.exploreWithMe.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.exploreWithMe.category.mapper.CategoryMapper;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.event.dto.EventDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.dto.LocationDto;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.user.mapper.UserMapper;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventMapper {

    public static Event toEvent(NewEventDto eventDto, Category category, User user) {
        Event event = new Event();
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setLat(eventDto.getLocation().getLat());
        event.setLon(eventDto.getLocation().getLon());
        event.setPaid(eventDto.getPaid() != null ? eventDto.getPaid() : false);
        event.setParticipantLimit(eventDto.getParticipantLimit() != null ? eventDto.getParticipantLimit() : 0);
        event.setRequestModeration(eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() : true);
        event.setTitle(eventDto.getTitle());
        event.setInitiator(user);
        return event;
    }

    public static EventDto toEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setAnnotation(event.getAnnotation());
        eventDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventDto.setConfirmedRequests(event.getConfirmedRequests());
        eventDto.setCreatedOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventDto.setDescription(event.getDescription());
        eventDto.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventDto.setLocation(new LocationDto(event.getLat(), event.getLon()));
        eventDto.setPaid(event.isPaid());
        eventDto.setParticipantLimit(event.getParticipantLimit());
        eventDto.setPublishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        eventDto.setRequestModeration(event.getRequestModeration());
        eventDto.setState(event.getState());
        eventDto.setTitle(event.getTitle());
        return eventDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setTitle(event.getTitle());

        return eventShortDto;
    }

    public static List<EventShortDto> toEventShortDtoList(List<Event> events) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : events) {
            eventShortDtoList.add(toEventShortDto(event));
        }
        return eventShortDtoList;
    }
}