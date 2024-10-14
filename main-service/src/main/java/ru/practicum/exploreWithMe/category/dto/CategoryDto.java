package ru.practicum.exploreWithMe.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @Size(max = 50, message = "Имя не должно быть длинее 50 символов")
    @NotBlank
    private String name;
}