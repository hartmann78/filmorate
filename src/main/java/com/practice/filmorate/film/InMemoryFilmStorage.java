package com.practice.filmorate.film;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.exceptions.ValidationException;
import com.practice.filmorate.user.UserStorage;
import com.practice.filmorate.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private UserStorage userStorage;

    private long globalFilmId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Autowired
    @Qualifier("inMemoryUserStorage")
    private void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findFilmById(Long filmId) {
        return films.values()
                .stream()
                .filter(x -> x.getId().equals(filmId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден!"));
    }

    @Override
    public Collection<Film> topFilms(int count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId()))
            throw new ValidationException("Фильм уже содержится в базе данных!");
        film.setId(++globalFilmId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId()) && film.getId() != 0) {
            throw new NotFoundException(String.format("Фильм с данным id: %d не содержится в базе данных!", film.getId()));
        } else {
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
            } else {
                film.setId(++globalFilmId);
                films.put(film.getId(), film);
            }
            return film;
        }
    }

    @Override
    public void deleteFilm(Film film) {
        films.remove(film.getId(), film);
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        user.getLikedFilms().add(filmId);

        film.setLikes(film.getLikes() + 1);
    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        if (!user.getLikedFilms().contains(filmId)) {
            return;
        }

        user.getLikedFilms().remove(filmId);

        film.setLikes(film.getLikes() - 1);
    }
}
