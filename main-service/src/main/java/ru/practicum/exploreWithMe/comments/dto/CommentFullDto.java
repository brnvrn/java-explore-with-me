package ru.practicum.exploreWithMe.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentFullDto {
    private Long id;
    private String text;
    private UserShortDto commentator;
    private EventShortDto event;
    private CommentStatus status;
    private Integer points;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}