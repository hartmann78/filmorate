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
    private UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("userDbStorage")
    private void setUserStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        String sqlGenres = "select distinct genre_id from films_genres where film_id = ? order by genre_id";
        List<Long> genresId = jdbcTemplate.queryForList(sqlGenres, Long.class, resultSet.getLong("film_id"));
        List<Genre> genres = new ArrayList<>();

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
        if (film.getMpa() != null) {
            checkMpa(film);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            checkGenres(film);
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        if (film.getMpa() != null) {
            films_mpa_insert(id, film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            films_genres_insert(id, film.getGenres());
        }

        return findFilmById(id);
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
            films_mpa_delete(film.getId());
            films_mpa_insert(film.getId(), film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            films_genres_delete(film.getId());
            films_genres_insert(film.getId(), film.getGenres());
        }

        return findFilmById(film.getId());
    }

    @Override
    public void deleteFilm(Film film) {
        films_mpa_delete(film.getId());
        films_genres_delete(film.getId());

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

        String sql1 = "insert into users_like_films (user_id, film_id) values (?, ?)";
        jdbcTemplate.update(sql1, userId, filmId);

        String sql2 = "update films set likes = " +
                "(select count(user_id) from users_like_films where film_id = ?) " +
                "where film_id = ?";
        jdbcTemplate.update(sql2, filmId, filmId);
    }

    @Override
    public void dislikeFilm(Long filmId, Long userId) {
        findFilmById(filmId);
        userStorage.findUserById(userId);

        String sql1 = "delete from users_like_films where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql1, userId, filmId);

        String sql2 = "update films set likes = " +
                "(select count(user_id) from users_like_films where film_id = ?) " +
                "where film_id = ?";
        jdbcTemplate.update(sql2, filmId, filmId);
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

    private void films_mpa_insert(Long filmId, Long mpaId) {
        String sql = "insert into films_mpa (film_id, mpa_id) values (?, ?)";
        jdbcTemplate.update(sql, filmId, mpaId);
    }

    private void films_genres_insert(Long filmId, List<Genre> genres) {
        String sql = "insert into films_genres (film_id, genre_id) values (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    private void films_mpa_delete(Long filmId) {
        String sql1 = "delete from films_mpa where film_id = ?";
        jdbcTemplate.update(sql1, filmId);
    }

    private void films_genres_delete(Long filmId) {
        String sql2 = "delete from films_genre where film_id = ?";
        jdbcTemplate.update(sql2, filmId);
    }
}
