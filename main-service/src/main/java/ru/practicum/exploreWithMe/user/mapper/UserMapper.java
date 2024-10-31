package ru.practicum.exploreWithMe.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.dto.UserShortDto;
import ru.practicum.exploreWithMe.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public static User toUser(NewUserRequest NewUserRequest) {
        User user = new User();
        user.setEmail(NewUserRequest.getEmail());
        user.setName(NewUserRequest.getName());
        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            userDtoList.add(toUserDto(user));
        }
        return userDtoList;
    }
}
