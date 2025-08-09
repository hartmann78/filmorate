package com.practice.filmorate.film;

import com.practice.filmorate.genre.Genre;
import com.practice.filmorate.mpa.Mpa;
import com.practice.filmorate.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {
    private Long id;
    private int likes;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private List<User> likedUsers;
    private Set<Genre> genres;
    private Mpa mpa;

    public long getDuration() {
        return duration.getSeconds();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("likes", likes);
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("liked_users", likedUsers);
        values.put("genres", genres);
        values.put("mpa", mpa);
        return values;
    }
}
