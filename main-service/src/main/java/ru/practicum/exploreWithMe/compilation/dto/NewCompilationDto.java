package ru.practicum.exploreWithMe.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class NewCompilationDto {
    private boolean pinned;
    private List<Long> events;
    @NotBlank
    @Size(max = 50, message = "Название не должно быть больше 50 символов")
    private String title;
}