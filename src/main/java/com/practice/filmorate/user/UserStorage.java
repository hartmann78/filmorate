package com.practice.filmorate.user;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findUserById(Long userId);

    Collection<User> getFriendsList(Long userId);

    Collection<User> commonFriends(Long userId, Long otherId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}
