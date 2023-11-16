package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    @Query("select a from avatar a where a.id = :avatarId and a.active = 1")
    Optional<Avatar> findAvatarById(@Param("avatarId") Long avatarId);

}
