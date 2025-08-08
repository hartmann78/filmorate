package com.practice.filmorate.user;

import com.practice.filmorate.film.Film;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;
    private Set<User> friends;
    private Set<Film> likedFilms;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("login", login);
        values.put("name", name);
        values.put("email", email);
        values.put("birthday", birthday);
        values.put("friends", friends);
        values.put("likedFilms", likedFilms);
        return values;
    }
}
