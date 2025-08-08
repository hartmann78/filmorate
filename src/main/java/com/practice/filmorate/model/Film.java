package com.practice.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Set<Long> likedUsers = new HashSet<>();

    @NotNull
    private long id;

    @NotNull
    private int likes;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    private Duration duration;

    public long getDuration() {
        return duration.getSeconds();
    }
}
