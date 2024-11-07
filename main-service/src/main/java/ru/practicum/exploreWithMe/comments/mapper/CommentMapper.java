package ru.practicum.exploreWithMe.comments.mapper;

import org.mapstruct.*;
import ru.practicum.exploreWithMe.comments.dto.*;
import ru.practicum.exploreWithMe.comments.model.Comment;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    Comment toComment(CommentDto newCommentDto,
                      User commentator,
                      Event event,
                      CommentStatus status,
                      LocalDateTime created);

    CommentShortDto toCommentShortDto(Comment comment);

    CommentFullDto toCommentFullDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void setUpdateCommentDto(@MappingTarget Comment comment, UpdateCommentDto updateCommentDto);
}
