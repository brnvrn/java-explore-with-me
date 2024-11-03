package ru.practicum.exploreWithMe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Setter
@Getter
@Accessors(chain = true)
public class EndpointHits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Size(max = 30, message = "Поле app не должно превышать 30 символов")
    private String app;
    @Column(nullable = false)
    @Size(max = 255, message = "Поле uri не должно превышать 255 символов")
    private String uri;
    @Column(nullable = false)
    @Size(max = 15, message = "Поле ip не должно превышать 15 символов")
    private String ip;
    @Column(name = "create_date")
    private LocalDateTime timestamp;
}