package com.example.hw5.repository;

import com.example.hw5.data.GenreData;
import com.example.hw5.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreRepository {

    @Autowired
    private DataSource dataSource;

    private NamedParameterJdbcTemplate jdbcTemplate;


    @PostConstruct
    public void init() {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }



    public GenreData get(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from genre where id = :id",
                    new MapSqlParameterSource().addValue("id", id),
                    (rs, rowNum) -> toGenreData(rs));
        }catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre with id %d not found".formatted(id));
        }

    }

    private GenreData toGenreData(ResultSet rs) throws SQLException {
        return GenreData.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    public List<String> getAllGenre() {
            return jdbcTemplate.queryForList("select name from genre", new MapSqlParameterSource(),
                  String.class);
    }
}
