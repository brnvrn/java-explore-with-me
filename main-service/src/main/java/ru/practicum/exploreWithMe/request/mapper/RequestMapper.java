package ru.practicum.exploreWithMe.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "participationRequest.requestStatus", target = "status")
    @Mapping(source = "participationRequest.event.id", target = "event")
    @Mapping(source = "participationRequest.requester.id", target = "requester")
    RequestDto toRequestDto(Request participationRequest);
}