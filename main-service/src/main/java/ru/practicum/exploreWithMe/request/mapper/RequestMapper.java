package ru.practicum.exploreWithMe.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.exploreWithMe.request.dto.RequestDto;
import ru.practicum.exploreWithMe.request.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    RequestDto toRequestDto(Request request);

    @Mapping(target = "request", ignore = true)
    List<RequestDto> toRequestDtoList(List<Request> requestsList);
}