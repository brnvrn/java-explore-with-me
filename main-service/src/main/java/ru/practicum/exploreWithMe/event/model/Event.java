package ru.practicum.exploreWithMe.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotBlank
    @Size(max = 2000, message = "Аннотация не должна превышать 2000 символов")
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(nullable = false)
    @NotBlank
    @Size(max = 7000, message = "Описание не должно превышать 7000 символов")
    private String description;
    @Column(name = "event_date", nullable = false)
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator", nullable = false)
    private User initiator;
    @Column(nullable = false)
    private float lat;
    @Column(nullable = false)
    private float lon;
    @Column(nullable = false)
    private boolean paid;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Column(name = "published_on", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state;
    @Column(nullable = false)
    @NotBlank
    @Size(max = 120, message = "Заголовок не должен превышать 120 символов")
    private String title;
}