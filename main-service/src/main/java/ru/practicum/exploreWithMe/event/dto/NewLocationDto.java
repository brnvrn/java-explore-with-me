package ru.practicum.exploreWithMe.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class NewLocationDto {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
