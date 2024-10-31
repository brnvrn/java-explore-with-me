package ru.practicum.exploreWithMe.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.event.dto.EventDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    Event toEvent(NewEventDto eventDto, Category category, User initiator);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
    EventDto toEventDto(Event event);

    @Mapping(target = "views", ignore = true)
    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDtoList(List<Event> events);
}