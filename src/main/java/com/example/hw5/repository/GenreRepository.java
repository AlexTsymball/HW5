package com.example.hw5.repository;

import com.example.hw5.data.GenreData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@AllArgsConstructor
@Transactional
public class GenreRepository {

    private EntityManager entityManager;

    public List<GenreData> getAllGenre() {
        String sql = "SELECT * FROM genre";
        return entityManager.createNativeQuery(sql, GenreData.class)
                    .getResultList();
    }

    public GenreData getGenre(int id) {

        return entityManager.find(GenreData.class, id);
    }
}
