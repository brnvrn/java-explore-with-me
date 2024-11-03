package ru.practicum.exploreWithMe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.exploreWithMe.dto.StatisticsDto;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
    private String app;
    private String uri;
    private Long hits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticsDto that = (StatisticsDto) o;
        return Objects.equals(hits, that.getHits());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hits);
    }
}