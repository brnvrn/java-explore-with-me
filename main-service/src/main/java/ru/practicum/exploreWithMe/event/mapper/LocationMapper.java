package ru.practicum.exploreWithMe.event.mapper;

import org.mapstruct.*;
import ru.practicum.exploreWithMe.event.dto.NewLocationDto;
import ru.practicum.exploreWithMe.event.model.Location;


@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    Location toLocation(NewLocationDto newLocationDto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    void setLocation(@MappingTarget Location location, NewLocationDto newLocationDto);
}
