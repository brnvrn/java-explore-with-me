package ru.practicum.exploreWithMe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StatisticsDto {
    private String app;
    private String uri;
    private Long hits;
}
