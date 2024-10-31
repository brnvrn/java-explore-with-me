package ru.practicum.exploreWithMe.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.exploreWithMe.dto.EndpointHitsDto;
import ru.practicum.exploreWithMe.dto.StatisticsDto;
import ru.practicum.exploreWithMe.model.EndpointHits;
import ru.practicum.exploreWithMe.model.Statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class EndpointHitMapper {
    public static EndpointHits toEndpointHits(EndpointHitsDto endpointHitsDto) {
        EndpointHits hit = new EndpointHits();
        hit.setApp(endpointHitsDto.getApp());
        hit.setUri(endpointHitsDto.getUri());
        hit.setIp(endpointHitsDto.getIp());
        hit.setTimestamp(endpointHitsDto.getTimestamp());
        return hit;
    }

    public static EndpointHitsDto toEndpointHitsDto(EndpointHits hit) {
        return new EndpointHitsDto(hit.getId(), hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }

    private static StatisticsDto toStatisticsDto(Statistics statistics) {
        return new StatisticsDto(statistics.getApp(), statistics.getUri(), statistics.getHits());
    }

    public static List<StatisticsDto> toStatisticsDtoList(List<Statistics> stats) {
        List<StatisticsDto> statsDroList = new ArrayList<>();
        for (Statistics viewStats : stats) {
            statsDroList.add(toStatisticsDto(viewStats));
        }
        statsDroList.sort(Comparator.comparingLong(StatisticsDto::getHits).reversed());
        return statsDroList;
    }
}
