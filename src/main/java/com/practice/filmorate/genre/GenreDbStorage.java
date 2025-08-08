package com.practice.filmorate.genre;

import com.practice.filmorate.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "select genre_id, name from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
//                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public Genre findGenreById(Long genreId) {
        String sqlQuery = "select name from genres where genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
        } catch (Exception e) {
            throw new NotFoundException("В базе данных не найден жанр с id: " + genreId);
        }
    }

    @Override
    public Genre createGenre(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("genre_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(genre.toMap()).longValue();
        genre.setId(id);
        return genre;
    }

    @Override
    public Genre updateGenre(Genre genre) {
        String sqlQuery = "update genres set name = ? where genre_id = ?";
        jdbcTemplate.update(sqlQuery,
//                genre.getName(),
                genre.getId());
        return genre;
    }

    @Override
    public void deleteGenre(Genre genre) {
        String sqlQuery = "delete from genres where genre_id = ? and name = ?";
        jdbcTemplate.update(sqlQuery,
                genre.getId());
//                genre.getName());
    }
}
