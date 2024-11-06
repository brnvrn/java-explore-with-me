package ru.practicum.exploreWithMe.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);
}