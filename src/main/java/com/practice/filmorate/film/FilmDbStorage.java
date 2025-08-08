package com.practice.filmorate.film;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.genre.Genre;
import com.practice.filmorate.genre.GenreStorage;
import com.practice.filmorate.mpa.MpaDbStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final MpaDbStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "select film_id, likes, name, description, release_date, duration, mpa from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .likes(resultSet.getInt("likes"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofSeconds(resultSet.getLong("duration")))
                .mpa(resultSet.getLong("mpa"))
                .build();
    }

    @Override
    public Film findFilmById(Long filmId) {
        String sqlQuery = "select film_id, likes, name, description, release_date, duration, mpa from films where film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } catch (Exception e) {
            throw new NotFoundException("В базе данных не найден фильм с id: " + filmId);
        }
    }

    @Override
    public Collection<Film> topFilms(int count) {
        return List.of();
    }

    @Override
    public Film createFilm(Film film) {
//        checkMpa(film);
//        checkGenres(film);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
//        checkMpa(film);
//        checkGenres(film);

        String sqlQuery = "update films set " +
                "likes = ?, " +
                "name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ? " +
                "mpa = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getLikes(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId());
        return film;
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {

    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {

    }

    @Override
    public void deleteFilm(Film film) {

    }

    private void checkMpa(Film film) {
        Long id = film.getMpa();
        mpaStorage.findMpaById(id);
    }

    private void checkGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            genreStorage.findGenreById(genre.getId());
        }
    }
}
