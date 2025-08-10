package com.practice.filmorate.user;

import java.util.Collection;

public interface UserService {
    Collection<User> findAll();

    User findUserById(Long userId);

    Collection<User> getFriendsList(Long userId);

    Collection<User> commonFriends(Long userId, Long otherId);

    User createUser(User User);

    User updateUser(User User);

    void deleteUser(User user);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void checkUser(User user);
}
