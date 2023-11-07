package com.cherish.backend.repositroy;

import com.cherish.backend.domain.BackUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackUpRepository extends JpaRepository<BackUp, String> {

    @Query("select b from BackUp b where b.id = :id")
    Optional<BackUp> findBackUpById(@Param("id") String id);

}
