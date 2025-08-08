package com.practice.filmorate.controller;

import com.practice.filmorate.model.User;
import com.practice.filmorate.storages.InMemoryUserStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;

    @GetMapping("/users")
    public Collection<User> findAll(HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.findAll();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.updateUser(user);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}