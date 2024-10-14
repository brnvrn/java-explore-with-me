package ru.practicum.exploreWithMe.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private boolean pinned;
    private List<EventShortDto> events;
    @NotBlank
    @Size(max = 50, message = "Название не должно быть больше 50 символов")
    private String title;
}
