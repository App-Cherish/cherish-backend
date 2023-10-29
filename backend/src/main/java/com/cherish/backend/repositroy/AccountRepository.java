package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select a from account a where a.oauthId = :oauthId and a.active = 1")
    Optional<Account> findAccountByOauthId(@Param(value = "oauthId") String oauthId);

}
