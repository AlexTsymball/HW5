package com.example.hw5.repository;

import com.example.hw5.data.BookData;
import com.example.hw5.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@Transactional
public class BookRepository {

    private EntityManager entityManager;


    public int save(BookData book) {
        entityManager.persist(book);
        return book.getId();
    }

    public BookData getBookById(int id) {
        return entityManager.find(BookData.class, id);
    }


    public List<BookData> getAllBook(Map<String, Object> params) {
        String sql = generatePaginatorSql(new StringBuilder("select * from book  "), params);
        Query query = entityManager.createNativeQuery(sql, BookData.class);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        return query.getResultList();
    }

    public List<BookData> searchByNameAndOrGroup(Map<String, Object> params) {
        String sql = generateSearchSql(params);
        Query query = entityManager.createQuery(sql, BookData.class);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        return query.getResultList();
    }

    private String generateSearchSql(Map<String, Object> params) {
        StringBuilder result = new StringBuilder("select b from book b ");
        StringBuilder where = new StringBuilder();
        if (params.containsKey("name")) {
            where.append("b.name = :name ");
        }
        if (params.containsKey("genre")) {
            if (!where.isEmpty()) {
                where.append("and ");
            }
            where.append("b.genre.name = :genre");
        }
        if (!where.isEmpty()) {
            result.append("where ").append(where);
        }

        return generatePaginatorSql(result, params);
    }

    private String generatePaginatorSql(StringBuilder result, Map<String, Object> params) {
        if (params.containsKey("limit")) {
            result.append("limit :limit ");
            if (params.containsKey("offset")) {
                result.append("offset :offset ");
            }
        }

        return String.valueOf(result);
    }

    public void delete(int id) {
        String sql = "delete from book where id = :id";
        int deleted = entityManager.createNativeQuery(sql)
                .setParameter("id", id)
                .executeUpdate();
        if (deleted < 1) {
            throw new NotFoundException("Book with id %d not found".formatted(id));
        }
    }

    public void deleteAll() {
        String sql = "TRUNCATE TABLE book";
        entityManager.createNativeQuery(sql)
                .executeUpdate();
    }
}