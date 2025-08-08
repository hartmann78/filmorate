package com.practice.filmorate.film;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.exceptions.ValidationException;
import com.practice.filmorate.user.UserStorage;
import com.practice.filmorate.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("filmStorageImpl")
public class FilmStorageImpl implements FilmStorage {
    private long globalFilmId = 0;
    @Autowired
    @Qualifier("userStorageImpl")
    private UserStorage userStorage;
    private final Map<Long, Film> films = new HashMap<>();

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

        user.getLikedFilms().add(film);
        film.getLikedUsers().add(user);

        film.setLikes(film.getLikedUsers().size());
    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);

        if (!user.getLikedFilms().contains(film) || !film.getLikedUsers().contains(user)) {
            return;
        }

        user.getLikedFilms().remove(film);
        film.getLikedUsers().remove(user);

        film.setLikes(film.getLikedUsers().size());
    }
}
