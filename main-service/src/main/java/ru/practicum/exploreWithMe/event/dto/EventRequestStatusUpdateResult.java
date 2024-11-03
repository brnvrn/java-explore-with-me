package ru.practicum.exploreWithMe.event.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.exploreWithMe.request.dto.RequestDto;

import java.util.List;


@Setter
@Getter
@Accessors(chain = true)
public class EventRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
