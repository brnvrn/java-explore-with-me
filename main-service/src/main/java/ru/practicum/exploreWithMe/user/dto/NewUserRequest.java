package ru.practicum.exploreWithMe.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(max = 50, message = "Эмейл не должен быть больше 50 символов")
    private String email;
    @NotBlank
    @Size(max = 255, message = "Имя не должно быть больше 255 символов")
    private String name;
}
