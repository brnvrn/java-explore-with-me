package ru.practicum.exploreWithMe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
public class StatisticsDto {
    private String app;
    private String uri;
    private Long hits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticsDto that = (StatisticsDto) o;
        return Objects.equals(hits, that.hits);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hits);
    }
}
