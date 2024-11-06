package ru.practicum.exploreWithMe.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.exploreWithMe.request.model.RequestStatus;

import java.time.LocalDateTime;


@Getter
@Setter
@Accessors(chain = true)
public class RequestDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}