package com.cherish.backend.repositroy;

import com.cherish.backend.domain.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {

    @Query(value = "select s from session_token s where s.sessionTokenVaule = :tokenValue and s.active = 1")
    Optional<SessionToken> findSessionTokenBySessionTokenValue(@Param("tokenValue") String tokenValue);

    @Query(value = "select s from session_token s where s.deviceId=:deviceId")
    Optional<SessionToken> findSessionTokenByDeviceId(@Param("deviceId") String deviceId);

    @Query(value = "select count(s.deviceId)>0  from session_token s where s.deviceId=:deviceId")
    Boolean findExistTokenByDeviceId(@Param("deviceId") String deviceId);

}
