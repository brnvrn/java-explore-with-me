package ru.practicum.exploreWithMe.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.practicum.exploreWithMe.request.model.RequestStatus;

import java.util.List;

@Getter
@NotNull
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}