package ru.practicum.exploreWithMe.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    Long id;
    @NotBlank
    @Size(max = 255, message = "Имя не должно быть больше 255 символов")
    String name;
}
