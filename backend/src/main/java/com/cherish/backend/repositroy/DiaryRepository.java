package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {

    @Query("update diary d set d.backUp.id = :backUpId where d.id = :id")
    void updateDiaryByBackUp(@Param("backUpId") String backUpId, @Param("id") String id);


}
