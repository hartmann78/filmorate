package com.practice.filmorate.controller;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.storages.InMemoryFilmStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @GetMapping("/films")
    public Collection<Film> findAll(HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.findAll();
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.updateFilm(film);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}