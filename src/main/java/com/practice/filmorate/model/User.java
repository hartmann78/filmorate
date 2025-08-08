package com.practice.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Component
public class User {
    private Set<Long> friends = new HashSet<>();
    private Set<Long> likedFilms = new HashSet<>();

    @NotNull
    private long id;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @NotNull
    private LocalDate birthday;
}
