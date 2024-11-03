package ru.practicum.exploreWithMe.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.exploreWithMe.category.model.Category;
import ru.practicum.exploreWithMe.user.model.User;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "events")
@Accessors(chain = true)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category eventCategory;
    @Column(nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location eventLocation;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    private String title;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "published_date", nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
    @Column(name = "event_state")
    @Enumerated(EnumType.STRING)
    private EventState state;
    private Long views;
}