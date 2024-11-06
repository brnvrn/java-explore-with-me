package ru.practicum.exploreWithMe.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Название не должно быть больше 50 символов")
    private String title;
}