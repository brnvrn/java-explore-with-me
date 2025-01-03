package ru.practicum.exploreWithMe.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.comments.dto.CommentFullDto;
import ru.practicum.exploreWithMe.comments.dto.CommentStatus;
import ru.practicum.exploreWithMe.comments.mapper.CommentMapper;
import ru.practicum.exploreWithMe.comments.model.Comment;
import ru.practicum.exploreWithMe.comments.repository.CommentRepository;
import ru.practicum.exploreWithMe.exception.NotFoundException;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentFullDto updateCommentStatus(Long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new NotFoundException("Такой комментарий не найден"));
        if (!comment.getStatus().equals(CommentStatus.PENDING)) {
            throw new IllegalArgumentException("Комментарий не находится на рассмотрении");
        }
        if (comment.getPoints() > 3) {
            comment.setStatus(CommentStatus.PUBLISHED_POSITIVE);
        } else {
            comment.setStatus(CommentStatus.PUBLISHED_NEGATIVE);
        }
        commentRepository.save(comment);
        log.info("Изменение статуса комментария с id ={}", comId);
        return commentMapper.toCommentFullDto(comment);
    }

    public CommentFullDto getCommentById(Long comId) {
        Comment comment = commentRepository.findById(comId).orElseThrow(
                () -> new NotFoundException("Такой комментарий не найден"));
        log.info("Получения комментария с id ={}", comId);
        return commentMapper.toCommentFullDto(comment);
    }
}
