package com.practice.filmorate.interfaces;

import com.practice.filmorate.model.User;

public interface UserStorage {
    User createUser(User user);

    boolean deleteUser(User user);

    User updateUser(User user);
}
