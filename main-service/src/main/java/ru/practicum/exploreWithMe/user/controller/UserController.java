package ru.practicum.exploreWithMe.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.user.dto.NewUserRequest;
import ru.practicum.exploreWithMe.user.dto.UserDto;
import ru.practicum.exploreWithMe.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> addNewUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Получен POST-запрос на добавление нового пользователя: {}", newUserRequest);
        return ResponseEntity.status(201).body(userService.addNewUser(newUserRequest));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive Long userId) {
        log.info("Получен DELETE-запрос на удаление пользователя с id={}", userId);
        userService.deleteUserById(userId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") int from,
                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получен GET-запрос на получение списка пользователей с параметрами: ids={}, from={}, size={}", ids,
                from, size);
        return userService.getUsers(ids, from, size);
    }
}
