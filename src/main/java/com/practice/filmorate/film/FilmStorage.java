package com.practice.filmorate.film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film findFilmById(Long filmId);

    Collection<Film> topFilms(int count);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    void likeFilm(Long filmId, Long userId);

    void dislikeFilm(Long filmId, Long userId);
}
