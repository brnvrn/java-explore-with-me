package ru.practicum.exploreWithMe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class EndpointHitsDto {
    @NotBlank
    @Size(max = 30, message = "Поле app не должно превышать 30 символов")
    private String app;
    @NotBlank
    @Size(max = 15, message = "Поле ip не должно превышать 15 символов")
    private String ip;
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    @NotBlank
    @Size(max = 255, message = "Поле uri не должно превышать 255 символов")
    private String uri;
}