package com.practice.filmorate.mpa;

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
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "select mpa_id, name from mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
//                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public Mpa findMpaById(Long mpaId) {
        String sqlQuery = "select name from mpa where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (Exception e) {
            throw new NotFoundException("В базе данных не найден рейтинг с id: " + mpaId);
        }
    }

    @Override
    public Mpa createMpa(Mpa mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("mpa")
                .usingGeneratedKeyColumns("mpa_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(mpa.toMap()).longValue();
        mpa.setId(id);
        return mpa;
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        String sqlQuery = "update mpa set name = ? where mpa_id = ?";
        jdbcTemplate.update(sqlQuery,
//                mpa.getName(),
                mpa.getId());
        return mpa;
    }

    @Override
    public void deleteMpa(Mpa mpa) {
        String sqlQuery = "delete from mpa where mpa_id = ? and name = ?";
        jdbcTemplate.update(sqlQuery,
                mpa.getId());
//                mpa.getName());
    }
}
