package com.practice.filmorate.service;

import com.practice.filmorate.model.Film;
import com.practice.filmorate.model.User;
import com.practice.filmorate.storages.InMemoryFilmStorage;
import com.practice.filmorate.storages.InMemoryUserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Collection<Film> topFilms(int count) {
        return inMemoryFilmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void likeFilm(long filmId, long userId) {
        Film film = inMemoryFilmStorage.findFilmById(filmId);
        User user = inMemoryUserStorage.findUserById(userId);

        Set<Long> userNewLikedFilms = user.getLikedFilms();
        Set<Long> filmNewLikedUsers = film.getLikedUsers();

        userNewLikedFilms.add(filmId);
        filmNewLikedUsers.add(userId);

        user.setLikedFilms(userNewLikedFilms);
        film.setLikedUsers(filmNewLikedUsers);

        film.setLikes(film.getLikedUsers().size());
    }

    public void dislikeFilm(long filmId, long userId) {
        Film film = inMemoryFilmStorage.findFilmById(filmId);
        User user = inMemoryUserStorage.findUserById(userId);

        if (!user.getLikedFilms().contains(filmId) || !film.getLikedUsers().contains(userId)) {
            return;
        }

        Set<Long> userNewLikedFilms = user.getLikedFilms();
        Set<Long> filmNewLikedUsers = film.getLikedUsers();

        userNewLikedFilms.remove(filmId);
        filmNewLikedUsers.remove(userId);

        user.setLikedFilms(userNewLikedFilms);
        film.setLikedUsers(filmNewLikedUsers);

        film.setLikes(film.getLikedUsers().size());
    }
}
