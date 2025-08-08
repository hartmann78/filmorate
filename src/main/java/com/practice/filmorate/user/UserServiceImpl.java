package com.practice.filmorate.user;

import com.practice.filmorate.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findUserById(Long userId) {
        return userStorage.findUserById(userId);
    }

    @Override
    public Collection<User> getFriendsList(Long userId) {
        return userStorage.getFriendsList(userId);
    }

    @Override
    public Collection<User> commonFriends(Long userId, Long otherId) {
        return userStorage.commonFriends(userId, otherId);
    }

    @Override
    public User createUser(User user) {
        checkUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        findUserById(user.getId());
        checkUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public void checkUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'!");
        }
        if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }
}
