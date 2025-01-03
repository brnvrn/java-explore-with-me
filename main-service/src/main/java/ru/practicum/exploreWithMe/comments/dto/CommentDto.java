package ru.practicum.exploreWithMe.comments.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDto {
    @NotBlank
    @Size(max = 2000, message = "Текст комментария не должен быть больше 2000 символов")
    private String text;
    @Min(1)
    @Max(5)
    private Integer points;
}
