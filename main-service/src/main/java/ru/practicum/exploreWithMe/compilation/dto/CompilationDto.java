package ru.practicum.exploreWithMe.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
