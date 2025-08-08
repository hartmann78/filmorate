package com.practice.filmorate.film;

import com.practice.filmorate.genre.GenreController;
import com.practice.filmorate.mpa.MpaController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final MpaController mpaController;
    private final GenreController genreController;

    @GetMapping("/films")
    public Collection<Film> findAll(HttpServletRequest request) {
        setLog(request);
        return filmService.findAll();
    }

    @GetMapping("/films/{filmId}")
    public Film findFilmById(@PathVariable Long filmId, HttpServletRequest request) {
        setLog(request);
        return filmService.findFilmById(filmId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> topFilms(@RequestParam(required = false, defaultValue = "10") int count, HttpServletRequest request) {
        setLog(request);
        return filmService.topFilms(count);
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") Long filmId, @PathVariable Long userId, HttpServletRequest request) {
        setLog(request);
        filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void dislikeFilm(@PathVariable("id") Long filmId, @PathVariable Long userId, HttpServletRequest request) {
        setLog(request);
        filmService.dislikeFilm(filmId, userId);
    }

    @DeleteMapping("/films")
    public void deleteFilm(@RequestBody Film film, HttpServletRequest request) {
        setLog(request);
        filmService.deleteFilm(film);
    }

    public void setLog(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}