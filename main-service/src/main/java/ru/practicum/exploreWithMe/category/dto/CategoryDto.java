package ru.practicum.exploreWithMe.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CategoryDto {
    private Long id;
    @Size(min = 1, max = 50, message = "Имя не должно быть длинее 50 символов")
    @NotBlank
    private String name;
}