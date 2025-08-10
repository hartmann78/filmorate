package com.practice.filmorate.film;

import com.practice.filmorate.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmServiceImpl implements FilmService {
    private FilmStorage filmStorage;

    @Autowired
    @Qualifier("filmDbStorage")
    private void setFilmStorage(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film findFilmById(Long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @Override
    public Collection<Film> topFilms(int count) {
        return filmStorage.topFilms(count);
    }

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        findFilmById(film.getId());
        checkFilm(film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {
        filmStorage.dislikeFilm(filmId, userId);
    }

    @Override
    public void checkFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым!");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года!");
        } else if (film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата релиза должна быть не позже " + LocalDate.now().getYear() + " года!");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }
}
