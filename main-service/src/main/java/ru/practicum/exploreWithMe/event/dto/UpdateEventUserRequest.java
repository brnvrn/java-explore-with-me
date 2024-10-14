package ru.practicum.exploreWithMe.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.event.model.PrivateStateAction;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @NotBlank
    @Size(max = 2000, message = "Аннотация не должна превышать 2000 символов")
    private String annotation;
    private Long category;
    @NotBlank
    @Size(max = 7000, message = "Описание не должно превышать 7000 символов")
    private String description;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Long participantLimit;
    private Boolean requestModeration;
    private PrivateStateAction privateStateAction;
    @NotBlank
    @Size(max = 120, message = "Заголовок не должен превышать 120 символов")
    private String title;
}