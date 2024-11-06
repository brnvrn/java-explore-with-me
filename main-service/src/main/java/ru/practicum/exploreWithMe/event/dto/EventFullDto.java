package ru.practicum.exploreWithMe.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.model.Location;
import ru.practicum.exploreWithMe.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Getter
@Setter
@Accessors(chain = true)
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private Category category;
    private Boolean paid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private String description;
    private Integer participantLimit;
    private EventState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private Location location;
    private Boolean requestModeration;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Long views;
}