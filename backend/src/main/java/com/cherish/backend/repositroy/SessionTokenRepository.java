package com.cherish.backend.repositroy;

import com.cherish.backend.domain.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {



}
