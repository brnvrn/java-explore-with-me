package ru.practicum.exploreWithMe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "endpoint_hits")
public class EndpointHits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @Size(max = 30, message = "Поле app не должно превышать 30 символов")
    private String app;
    @Column
    @Size(max = 30, message = "Поле uri не должно превышать 255 символов")
    private String uri;
    @Column
    @Size(max = 30, message = "Поле ip не должно превышать 15 символов")
    private String ip;
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;
}