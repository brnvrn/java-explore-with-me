package ru.practicum.exploreWithMe.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.mapper.UserMapper;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userMapper.toUser(newUserRequest);
        userRepository.save(user);
        log.info("Добавление нового пользователя: {}", newUserRequest);
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким айди не найден"));
        log.info("Удаление пользователя с id = {}", userId);
        userRepository.deleteById(userId);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids == null) {
            log.info("Получение списка пользователей с параметрами: from={}, size={}", from, size);
            return userRepository.findAll(page).stream()
                    .map(userMapper::toUserDto)
                    .toList();
        } else {
            log.info("Получение списка пользователей с параметрами: ids={}, from={}, size={}", ids, from, size);
            return userRepository.findByIdInOrderById(ids, page).stream()
                    .map(userMapper::toUserDto)
                    .toList();
        }
    }
}
