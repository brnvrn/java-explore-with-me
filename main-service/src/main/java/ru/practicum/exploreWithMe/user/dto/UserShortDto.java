package ru.practicum.exploreWithMe.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserShortDto {
    private Integer id;
    private String name;
}