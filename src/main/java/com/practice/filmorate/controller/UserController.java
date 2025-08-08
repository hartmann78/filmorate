package com.practice.filmorate.controller;

import com.practice.filmorate.model.User;
import com.practice.filmorate.service.UserService;
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
    private final UserService userService;
    private final InMemoryUserStorage inMemoryUserStorage;

    @GetMapping("/users")
    public Collection<User> findAll(HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.findAll();
    }

    @GetMapping("/users/{userId}")
    public User findUserById(@PathVariable long userId, HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.findUserById(userId);
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.updateUser(user);
    }

    @DeleteMapping("/users")
    public boolean deleteUser(@Valid @RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return inMemoryUserStorage.deleteUser(user);
    }


    @GetMapping("/users/{id}/friends")
    public Collection<User> friendsList(@PathVariable("id") long userId, HttpServletRequest request) {
        setLog(request);
        return userService.friendsList(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable("id") long userId, @PathVariable long otherId, HttpServletRequest request) {
        setLog(request);
        return userService.commonFriends(userId, otherId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long userId, @PathVariable long friendId, HttpServletRequest request) {
        setLog(request);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long userId, @PathVariable long friendId, HttpServletRequest request) {
        setLog(request);
        userService.deleteFriend(userId, friendId);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}