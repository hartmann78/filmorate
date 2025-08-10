package com.practice.filmorate.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public Collection<User> findAll(HttpServletRequest request) {
        setLog(request);
        return userService.findAll();
    }

    @GetMapping("/users/{userId}")
    public User findUserById(@PathVariable Long userId, HttpServletRequest request) {
        setLog(request);
        return userService.findUserById(userId);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user, HttpServletRequest request) {
        setLog(request);
        return userService.updateUser(user);
    }

    @DeleteMapping("/users")
    public void deleteUser(@RequestBody User user, HttpServletRequest request) {
        setLog(request);
        userService.deleteUser(user);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriendsList(@PathVariable("id") Long userId, HttpServletRequest request) {
        setLog(request);
        return userService.getFriendsList(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> commonFriends(@PathVariable("id") Long userId, @PathVariable Long otherId, HttpServletRequest request) {
        setLog(request);
        return userService.commonFriends(userId, otherId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable Long friendId, HttpServletRequest request) {
        setLog(request);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long userId, @PathVariable Long friendId, HttpServletRequest request) {
        setLog(request);
        userService.deleteFriend(userId, friendId);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}