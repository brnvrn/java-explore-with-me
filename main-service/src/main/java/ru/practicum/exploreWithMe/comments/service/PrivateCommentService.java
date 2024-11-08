package ru.practicum.exploreWithMe.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.comments.dto.CommentDto;
import ru.practicum.exploreWithMe.comments.dto.CommentFullDto;
import ru.practicum.exploreWithMe.comments.dto.CommentStatus;
import ru.practicum.exploreWithMe.comments.dto.UpdateCommentDto;
import ru.practicum.exploreWithMe.comments.mapper.CommentMapper;
import ru.practicum.exploreWithMe.comments.model.Comment;
import ru.practicum.exploreWithMe.comments.repository.CommentRepository;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.BadRequestException;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentFullDto addNewComment(Long userId, Long eventId, CommentDto commentDto) {
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким айди не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с таким айди не надено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Нельзя оставлять комментарии на неопубликованное событие");
        }
        Comment comment = commentMapper.toComment(
                commentDto,
                commentator,
                event,
                CommentStatus.PENDING,
                LocalDateTime.now().withNano(0)
        );
        commentRepository.save(comment);
        log.info("Добавление нового комментария от пользователя с id={} на событие с id={}", userId, eventId);
        return commentMapper.toCommentFullDto(comment);
    }

    @Transactional
    public CommentFullDto updateComment(Long userId, Long comId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new NotFoundException("Комментраий с таким айди не найден"));
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким айди не найден"));
        if (!commentator.equals(comment.getCommentator())) {
            throw new BadRequestException("Вы не владелец этого комментария");
        }
        commentMapper.setUpdateCommentDto(comment, updateCommentDto);
        commentRepository.save(comment);
        log.info("Изменение комментария с id ={} от пользователя с id ={}", comId, userId);
        return commentMapper.toCommentFullDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new NotFoundException("Комментраий с таким айди не найден"));
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким айди не найден"));
        if (!commentator.equals(comment.getCommentator())) {
            throw new BadRequestException("Вы не владелец этого комментария");
        }
        log.info("Удаление комментария с id ={} от пользователя с id ={}", comId, userId);
        commentRepository.delete(comment);
    }

    public CommentFullDto getComment(Long userId, Long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new NotFoundException("Комментраий с таким айди не найден"));
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким айди не найден"));
        if (!commentator.equals(comment.getCommentator())) {
            throw new BadRequestException("Вы не владелец этого комментария");
        }
        log.info("Получение комментария с id ={} от пользователя с id ={}", comId, userId);
        return commentMapper.toCommentFullDto(comment);
    }
}
