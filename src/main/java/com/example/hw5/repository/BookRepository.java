package com.example.hw5.repository;

//import com.example.hw5.dBuilt.MySqlDBUtil;

import com.example.hw5.data.BookData;
import com.example.hw5.dto.BookQueryDto;
import com.example.hw5.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GenreRepository genreRepository;

    private NamedParameterJdbcTemplate jdbcTemplate;


    @PostConstruct
    public void init() {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public int save(BookData book) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update("insert into book (name, author, id_genre) values (:name, :author, :id_genre)",
                new MapSqlParameterSource()
                        .addValue("name", book.getName())
                        .addValue("author", book.getAuthor())
                        .addValue("id_genre", book.getGenre().getId()),
                keyHolder);
        book.setId(keyHolder.getKey().intValue());
        return book.getId();
    }

    public BookData getBookById(int id) {
        try {
            return jdbcTemplate.queryForObject("select b.id, b.name, b.author, g.id, g.name from book as b JOIN genre as g ON b.id_genre = g.id where id = :id",
                    new MapSqlParameterSource().addValue("id", id),
                    BookData.class);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Book with id %d not found".formatted(id));
        }
    }


    public void updateBook(BookData data) {
        String sql = generateUpdateSql(data);
        int updated = jdbcTemplate.update(sql,
                new MapSqlParameterSource()
                        .addValue("id", data.getId())
                        .addValue("name", data.getName())
                        .addValue("author", data.getAuthor())
                        .addValue("id_genre", data.getGenre() == null ? null : data.getGenre().getId())

        );
        if (updated < 1) {
            throw new NotFoundException("Book with id %d not found".formatted(data.getId()));

        }
    }

    private String generateUpdateSql(BookData data) {
        StringBuilder result = new StringBuilder("update book set ");
        boolean oneParam = false;
        if (data.getName() != null && !data.getName().isBlank()) {
            result.append("name=:name");
            oneParam = true;
        }
        if (data.getAuthor() != null && !data.getAuthor().isBlank()) {
            if (oneParam) {
                result.append(",");
            }
            result.append(" author=:author");
        }

        if (data.getGenre() != null) {
            if (oneParam) {
                result.append(",");
            }
            result.append(" id_genre=:id_genre");
        }
        return String.valueOf(result.append(" where id = :id"));
    }

    public List<BookData> getAllBook(BookQueryDto query) {
        return searchByNameAndOrGroup(query);
    }

    public List<BookData> searchByNameAndOrGroup(BookQueryDto query) {
        String sql = generateSearchSql(query);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(query);

//        return jdbcTemplate.query(sql,
//                new MapSqlParameterSource()
//                        .addValue("name", query.getName())
//                        .addValue("genreId", query.getGenreId())
//                        .addValue("from", query.getFrom())
//                        .addValue("size", query.getSize())
//                ,
//                (rs, rowNum) -> toBookData(rs));

        return jdbcTemplate.queryForList(sql, namedParameters, BookData.class);

    }

    private String generateSearchSql(BookQueryDto query) {
        StringBuilder result = new StringBuilder("select b.id, b.name, b.author, g.id, g.name from book as b JOIN genre as g ON b.id_genre = g.id ");
        StringBuilder where = new StringBuilder();
        if (query.getName() != null && !query.getName().isBlank()) {
            where.append("name=:name ");
        }
        if (query.getGenreId() != null) {
            if(!where.isEmpty()) where.append("and ");
            where.append("id_genre=:genreId ");
        }
        if (!where.isEmpty()) result.append("where ").append(where);
        if (query.getSize() != 0 || query.getFrom() != 0) {
            result.append("limit :size offset :from");
        }
        return String.valueOf(result);
    }

    public void delete(int id) {
        int deleted = jdbcTemplate.update("delete from book  where id = :id",
                new MapSqlParameterSource()
                        .addValue("id", id));
        if (deleted < 1) {
            throw new NotFoundException("Book with id %d not found".formatted(id));

        }

    }

    public void deleteAll() {
        jdbcTemplate.update("delete from book", new MapSqlParameterSource());
//        jdbcTemplate.update("alter table book auto_increment = 1", new MapSqlParameterSource());

    }


//    private BookData toBookData(ResultSet rs) throws SQLException {
//        return BookData.builder()
//                .id(rs.getInt("id"))
//                .name(rs.getString("name"))
//                .author(rs.getString("author"))
//                .genre(genreRepository.get(rs.getInt("id_genre")))
//                .build();
//
//    }


}