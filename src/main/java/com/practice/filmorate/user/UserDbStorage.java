package com.practice.filmorate.user;

import com.practice.filmorate.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "select user_id, login, email, name, email, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public User findUserById(Long userId) {
        String sqlQuery = "select user_id, login, name, email, birthday from users where user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (Exception e) {
            throw new NotFoundException("В базе данных не найден пользователь с id: " + userId);
        }
    }

    @Override
    public Collection<User> getFriendsList(Long userId) {
        if (findUserById(userId) == null) {
            throw new NotFoundException("Не существует пользователя с id: " + userId);
        }

        String sqlQuery = "select * from users where user_id " +
                "in (select second_user_id from user_friendship where first_user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public Collection<User> commonFriends(Long userId, Long otherId) {
        Collection<User> firstUserFriends = getFriendsList(userId);
        Collection<User> secondUserFriends = getFriendsList(otherId);

        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .toList();
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set login = ?, " +
                "name = ?, " +
                "email = ?, " +
                "birthday = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void deleteUser(User user) {
        String sqlQuery = "delete from users where user_id = ?" +
                " and login = ?" +
                " and name = ?" +
                " and email = ?" +
                " and birthday = ?";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (findUserById(userId) == null) {
            throw new NotFoundException("Не существует пользователя с id: " + userId);
        }

        if (findUserById(friendId) == null) {
            throw new NotFoundException("Не существует пользователя с id: " + friendId);
        }

        String sqlQuery = "insert into user_friendship (first_user_id, second_user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        if (findUserById(userId) == null) {
            throw new NotFoundException("Не существует пользователя с id: " + userId);
        }

        if (findUserById(friendId) == null) {
            throw new NotFoundException("Не существует пользователя с id: " + friendId);
        }

        String sqlQuery = "delete from user_friendship where first_user_id = ? and second_user_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }
}
