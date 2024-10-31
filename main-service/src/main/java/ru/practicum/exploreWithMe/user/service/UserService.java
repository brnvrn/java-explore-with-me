package ru.practicum.exploreWithMe.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.exception.NotUniqueEmailException;
import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.mapper.UserMapper;
import ru.practicum.exploreWithMe.user.model.User;
import ru.practicum.exploreWithMe.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final Set<String> uniqueEmails = new HashSet<>();

    @Transactional
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        if (uniqueEmails.contains(newUserRequest.getEmail())) {
            throw new NotUniqueEmailException("Пользователь с таким email уже существует.");
        }

        User user = UserMapper.toUser(newUserRequest);
        User savedUser = userRepository.save(user);

        uniqueEmails.add(savedUser.getEmail());

        return UserMapper.toUserDto(savedUser);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userRepository.deleteById(userId);
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Page<User> page = userRepository.findByIdInOrderById(ids, PageRequest.of(from, size));
        return UserMapper.toUserDtoList(page.getContent());
    }
}
