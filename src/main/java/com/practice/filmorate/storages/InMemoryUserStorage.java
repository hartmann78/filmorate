package com.practice.filmorate.storages;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.exceptions.ValidationException;
import com.practice.filmorate.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage {
    private int userId = 0;
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User createUser(User user) {
        if (users.containsKey(user.getId()))
            throw new ValidationException("Пользователь уже содержится в базе данных!");
        checkUser(user);
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        checkUser(user);
        if (!users.containsKey(user.getId()) && user.getId() != 0) {
            throw new NotFoundException(String.format("Пользователь с данным id: %d не содержится в базе данных!", user.getId()));
        } else {
            if (users.containsKey(user.getId())) {
                users.replace(user.getId(), user);
            } else {
                user.setId(++userId);
                users.put(user.getId(), user);
            }
            return user;
        }
    }

    public void checkUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'!");
        } else if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }
}
