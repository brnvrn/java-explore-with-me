package ru.practicum.exploreWithMe.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.event.model.EventState;
import ru.practicum.exploreWithMe.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    @NotBlank
    @Size(max = 2000, message = "Аннотация не должна превышать 2000 символов")
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    @NotBlank
    @Size(max = 7000, message = "Описание не должно превышать 7000 символов")
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    @NotNull
    private LocationDto location;
    private boolean paid;
    @PositiveOrZero
    private Long participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private EventState eventState;
    @NotBlank
    @Size(max = 120, message = "Заголовок не должен превышать 120 символов")
    private String title;
    private Long views;
}
