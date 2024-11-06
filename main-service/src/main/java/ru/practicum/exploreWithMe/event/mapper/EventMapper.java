package ru.practicum.exploreWithMe.event.mapper;

import org.mapstruct.*;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.event.dto.*;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.model.Location;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "eventCategory", source = "categoryWithId")
    @Mapping(target = "eventLocation", source = "locationWithId")
    Event toEvent(NewEventDto newEventDto,
                  Location locationWithId,
                  Category categoryWithId,
                  User initiator,
                  LocalDateTime createdOn,
                  EventState state,
                  Integer confirmedRequests,
                  Long views
    );

    @Mapping(target = "location", source = "eventLocation")
    @Mapping(target = "category", source = "eventCategory")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", source = "event.eventCategory")
    EventShortDto toEventShortDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventCategory", ignore = true)
    @Mapping(target = "eventLocation", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    void updateEventByAdmin(@MappingTarget Event event, UpdateEventAdminRequest updateEventAdminRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventCategory", ignore = true)
    @Mapping(target = "eventLocation", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    void setUpdateEventUserRequest(@MappingTarget Event event, UpdateEventUserRequest updateEventUserRequest);
}