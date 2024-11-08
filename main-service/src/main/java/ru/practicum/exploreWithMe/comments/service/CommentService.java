package ru.practicum.exploreWithMe.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.comments.dto.CommentShortDto;
import ru.practicum.exploreWithMe.comments.dto.CommentSort;
import ru.practicum.exploreWithMe.comments.dto.CommentStatus;
import ru.practicum.exploreWithMe.comments.mapper.CommentMapper;
import ru.practicum.exploreWithMe.comments.model.Comment;
import ru.practicum.exploreWithMe.comments.repository.CommentRepository;
import ru.practicum.exploreWithMe.event.repository.EventRepository;
import ru.practicum.exploreWithMe.exception.NotFoundException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public List<CommentShortDto> getCommentsByEventId(Long eventId, String sort, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с таким айди не надено"));
        List<Comment> comments = commentRepository.findByEventIdOrderByCreatedDesc(eventId, pageable);
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }
        CommentSort commentSort = Enum.valueOf(CommentSort.class, sort);
        if (commentSort.equals(CommentSort.OLDEST)) {
            comments = comments.stream()
                    .sorted(Comparator.comparing(Comment::getCreated))
                    .toList();
        }
        if (commentSort.equals(CommentSort.NEGATIVE)) {
            comments = comments.stream()
                    .filter(comment -> comment.getStatus().equals(CommentStatus.PUBLISHED_NEGATIVE))
                    .toList();
        }
        if (commentSort.equals(CommentSort.POSITIVE)) {
            comments = comments.stream()
                    .filter(comment -> comment.getStatus().equals(CommentStatus.PUBLISHED_POSITIVE))
                    .toList();
        }

        log.info("Получение комментариев на событие с id={} с параметрами: from={}, size={}", eventId, from, size);
        return comments.stream()
                .map(commentMapper::toCommentShortDto)
                .toList();
    }
}
