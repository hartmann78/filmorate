package com.practice.filmorate.film;

import com.practice.filmorate.exceptions.NotFoundException;
import com.practice.filmorate.genre.Genre;
import com.practice.filmorate.genre.GenreStorage;
import com.practice.filmorate.mpa.Mpa;
import com.practice.filmorate.mpa.MpaStorage;
import com.practice.filmorate.user.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        String sqlGenres = "select genre_id from films_genres where film_id = ? order by genre_id";
        List<Long> genresId = jdbcTemplate.queryForList(sqlGenres, Long.class, resultSet.getLong("film_id"));
        Set<Genre> genres = new HashSet<>();

        for (Long genreId : genresId) {
            genres.add(genreStorage.findGenreById(genreId));
        }

        String sqlMpa = "select mpa_id from films_mpa where film_id = ?";
        Long mpaId = jdbcTemplate.queryForObject(sqlMpa, Long.class, resultSet.getLong("film_id"));
        Mpa mpa = mpaStorage.findMpaById(mpaId);

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .likes(resultSet.getInt("likes"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofSeconds(resultSet.getLong("duration")))
                .genres(genres)
                .mpa(mpa)
                .build();
    }

    @Override
    public Film findFilmById(Long filmId) {
        String sql = "select * from films where film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
        } catch (Exception e) {
            throw new NotFoundException("В базе данных не найден фильм с id: " + filmId);
        }
    }

    @Override
    public Collection<Film> topFilms(int count) {
        String sql = "select * from FILMS order by LIKES desc limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public Film createFilm(Film film) {
        checkMpa(film);
        checkGenres(film);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);

        films_mpa_insert(film);
        films_genres_insert(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getMpa() != null) {
            checkMpa(film);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            checkGenres(film);
        }

        String sql = "update films set " +
                "likes = ?, " +
                "name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sql,
                film.getLikes(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        if (film.getMpa() != null) {
            films_mpa_delete(film);
            films_mpa_insert(film);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            films_genres_delete(film);
            films_genres_insert(film);
        }
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        films_mpa_delete(film);
        films_genres_delete(film);

        String sql1 = "delete from film_liked_by_users where film_id = ?";
        jdbcTemplate.update(sql1, film.getId());

        String sql2 = "delete from films_liked_by_user where film_id = ?";
        jdbcTemplate.update(sql2, film.getId());

        String sql3 = "delete from film where film_id = ?" +
                " and likes = ?" +
                " and name = ?" +
                " and description = ?" +
                " and release_date = ?" +
                " and duration = ?";
        jdbcTemplate.update(sql3,
                film.getId(),
                film.getLikes(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId);

        String sql1 = "insert into film_liked_to_users (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql1, filmId, userId);

        String sql2 = "insert into films_liked_by_user (user_id, film_id) values (?, ?)";
        jdbcTemplate.update(sql2, userId, filmId);

        String sql3 = "update films set likes = likes + 1 where film_id = ?";
        jdbcTemplate.update(sql3, filmId);
    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId);

        String sql1 = "delete from film_liked_to_users where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql1, filmId, userId);

        String sql2 = "delete from films_liked_by_user where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql2, userId, filmId);

        String sql3 = "update films set likes = likes - 1 where film_id = ? and likes > -1";
        jdbcTemplate.update(sql3, filmId);
    }

    private void checkMpa(Film film) {
        Long id = film.getMpa().getId();
        mpaStorage.findMpaById(id);
    }

    private void checkGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            genreStorage.findGenreById(genre.getId());
        }
    }

    private void films_mpa_insert(Film film) {
        String sql = "insert into films_mpa (film_id, mpa_id) values (?, ?)";
        jdbcTemplate.update(sql, film.getId(), film.getMpa().getId());
    }

    private void films_genres_insert(Film film) {
        String sql = "insert into films_genres (film_id, genre_id) values (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    private void films_mpa_delete(Film film) {
        String sql1 = "delete from films_mpa where film_id = ?";
        jdbcTemplate.update(sql1, film.getId());
    }

    private void films_genres_delete(Film film) {
        String sql2 = "delete from films_genre where film_id = ?";
        jdbcTemplate.update(sql2, film.getId());
    }
}
