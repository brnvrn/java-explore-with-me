package ru.practicum.exploreWithMe.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Эмейл не должен быть больше 254 символов")
    private String email;
    @NotBlank
    @Size(min = 2, max = 250, message = "Имя не должно быть больше 255 символов")
    private String name;
}