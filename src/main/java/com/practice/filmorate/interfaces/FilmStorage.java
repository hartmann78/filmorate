package com.practice.filmorate.interfaces;

import com.practice.filmorate.model.Film;

public interface FilmStorage {
    Film createFilm(Film film);

    boolean deleteFilm(Film film);

    Film updateFilm(Film film);
}
