package com.practice.filmorate.service;

import com.practice.filmorate.model.User;
import com.practice.filmorate.storages.InMemoryUserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public Collection<User> friendsList(long userId) {
        User user = inMemoryUserStorage.findUserById(userId);
        Collection<User> list = new ArrayList<>();
        for (long i : user.getFriends()) {
            list.add(inMemoryUserStorage.findUserById(i));
        }
        return list;
    }

    public Collection<User> commonFriends(long userId, long otherId) {
        User user = inMemoryUserStorage.findUserById(userId);
        User other = inMemoryUserStorage.findUserById(otherId);

        Collection<User> userFriends = new ArrayList<>();
        Collection<User> otherFriends = new ArrayList<>();

        for (long k : user.getFriends()) {
            userFriends.add(inMemoryUserStorage.findUserById(k));
        }

        for (long j : other.getFriends()) {
            otherFriends.add(inMemoryUserStorage.findUserById(j));
        }

        return userFriends.stream().filter(otherFriends::contains).toList();
    }

    public void addFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.findUserById(userId);
        User friend = inMemoryUserStorage.findUserById(friendId);

        Set<Long> userNewFriendsList = user.getFriends();
        Set<Long> friendNewFriendsList = friend.getFriends();

        userNewFriendsList.add(friendId);
        friendNewFriendsList.add(userId);

        user.setFriends(userNewFriendsList);
        friend.setFriends(friendNewFriendsList);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.findUserById(userId);
        User friend = inMemoryUserStorage.findUserById(friendId);

        if (!user.getFriends().contains(friend.getId()) || !friend.getFriends().contains(user.getId())) {
            return;
        }

        Set<Long> userNewFriendsList = user.getFriends();
        Set<Long> friendNewFriendsList = friend.getFriends();

        userNewFriendsList.remove(friendId);
        friendNewFriendsList.remove(userId);

        user.setFriends(userNewFriendsList);
        friend.setFriends(friendNewFriendsList);
    }
}



