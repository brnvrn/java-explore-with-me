package ru.practicum.exploreWithMe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
    private String app;
    private String uri;
    private Long hits;
}