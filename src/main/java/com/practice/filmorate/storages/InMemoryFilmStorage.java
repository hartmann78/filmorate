package com.practice.filmorate.storages;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.exceptions.ValidationException;
import com.practice.filmorate.interfaces.FilmStorage;
import com.practice.filmorate.model.Film;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film findFilmById(long filmId) {
        return films.values()
                .stream()
                .filter(x -> x.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Фильм № %d не найден", filmId)));
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId()))
            throw new ValidationException("Фильм уже содержится в базе данных!");
        checkFilm(film);
        film.setId(++filmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilm(film);
        if (!films.containsKey(film.getId()) && film.getId() != 0) {
            throw new NotFoundException(String.format("Фильм с данным id: %d не содержится в базе данных!", film.getId()));
        } else {
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
            } else {
                film.setId(++filmId);
                films.put(film.getId(), film);
            }
            return film;
        }
    }

    @Override
    public boolean deleteFilm(Film film) {
        return films.remove(film.getId(), film);
    }

    public void checkFilm(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым!");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года!");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }
}
