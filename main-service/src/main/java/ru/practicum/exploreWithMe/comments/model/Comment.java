package ru.practicum.exploreWithMe.comments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.exploreWithMe.comments.dto.CommentStatus;
import ru.practicum.exploreWithMe.event.model.Event;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "comments")
@Accessors(chain = true)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String text;
    @ManyToOne
    @JoinColumn(name = "commentator_id", nullable = false)
    private User commentator;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @NotNull
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
    @NotNull
    private Integer points;
    @NotNull
    private LocalDateTime created;
}
