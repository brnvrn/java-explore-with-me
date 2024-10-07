package ru.practicum.exploreWithMe.mapper;

import org.mapstruct.Mapper;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.model.EndpointHits;
import ru.practicum.exploreWithMe.model.Statistics;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHits toEndpointHits(EndpointHitsDto endpointHitsDto);

    EndpointHitsDto toEndpointHitsDto(EndpointHits endpointHits);

    StatisticsDto toStatisticsDto(Statistics statistics);

    List<StatisticsDto> toStatisticsDtoList(List<Statistics> stats);
}
