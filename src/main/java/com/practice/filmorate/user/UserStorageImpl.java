package com.practice.filmorate.user;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("userStorageImpl")
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private Long globalUserId = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new NotFoundException(String.format("Пользователь № %d не найден", userId));
    }

    @Override
    public Collection<User> getFriendsList(Long userId) {
        return findUserById(userId).getFriends();
    }

    @Override
    public Collection<User> commonFriends(Long userId, Long otherId) {
        User user = findUserById(userId);
        User other = findUserById(otherId);

        Collection<User> userFriends = user.getFriends();
        Collection<User> otherFriends = other.getFriends();

        return userFriends.stream().filter(otherFriends::contains).toList();
    }

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId()))
            throw new ValidationException("Пользователь уже содержится в базе данных!");
        user.setId(++globalUserId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId()) && user.getId() != 0) {
            throw new NotFoundException(String.format("Пользователь с данным id: %d не содержится в базе данных!", user.getId()));
        } else {
            if (users.containsKey(user.getId())) {
                users.replace(user.getId(), user);
            } else {
                user.setId(++globalUserId);
                users.put(user.getId(), user);
            }
            return user;
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        user.getFriends().add(friend);
        friend.getFriends().add(user);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        if (!user.getFriends().contains(friend) || !friend.getFriends().contains(user)) {
            return;
        }

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId(), user);
    }
}
