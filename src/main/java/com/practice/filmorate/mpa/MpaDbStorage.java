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
        String sql = "select * from mpa order by mpa_id";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public Mpa findMpaById(Long mpaId) {
        String sql = "select * from mpa where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, mpaId);
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
        String sql = "update mpa set name = ? where mpa_id = ?";
        jdbcTemplate.update(sql,
                mpa.getName(),
                mpa.getId());
        return mpa;
    }

    @Override
    public void deleteMpa(Mpa mpa) {
        String sql = "delete from mpa where mpa_id = ? and name = ?";
        jdbcTemplate.update(sql,
                mpa.getId(),
                mpa.getName());
    }
}
