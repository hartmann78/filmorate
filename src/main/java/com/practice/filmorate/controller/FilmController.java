package com.practice.filmorate.controller;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.service.FilmService;
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
    private final FilmService filmService;
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @GetMapping("/films")
    public Collection<Film> findAll(HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.findAll();
    }

    @GetMapping("/films/{filmId}")
    public Film findFilmById(@PathVariable long filmId, HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.findFilmById(filmId);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.updateFilm(film);
    }

    @DeleteMapping("/films")
    public boolean deleteFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return inMemoryFilmStorage.deleteFilm(film);
    }


    @GetMapping("/films/popular")
    public Collection<Film> topFilms(@RequestParam(required = false, defaultValue = "10") int count, HttpServletRequest request) {
        setLog(request);
        return filmService.topFilms(count);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") long filmId, @PathVariable long userId, HttpServletRequest request) {
        setLog(request);
        filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void dislikeFilm(@PathVariable("id") long filmId, @PathVariable long userId, HttpServletRequest request) {
        setLog(request);
        filmService.dislikeFilm(filmId, userId);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}