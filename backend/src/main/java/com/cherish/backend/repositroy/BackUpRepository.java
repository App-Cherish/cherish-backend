package com.cherish.backend.repositroy;

import com.cherish.backend.domain.BackUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackUpRepository extends JpaRepository<BackUp, String> {

    @Query("select b from BackUp b where b.avatar.id = :avatarId and b.active = 1 order by b.createdDate DESC limit 1")
    Optional<BackUp> findBackUpByIdLatest(@Param("avatarId") Long avatarId);


    @Query("select count (b) > 0 from BackUp b where b.avatar.id = :avatarId and b.active = 1 ")
    Boolean ExistBackUpByAvatarId(@Param("avatarId") Long avatarId);

}
