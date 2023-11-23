package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Account;
import com.cherish.backend.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select a from account a where a.oauthId = :oauthId and a.active = 0")
    Optional<Account> findDeActiveAccountByOauthId(@Param(value = "oauthId") String oauthId);

    @Query("select a from account a where a.oauthId = :oauthId")
    Optional<Account> findAccountByOauthId(@Param(value = "oauthId") String oauthId);

    @Query("select a from account a where a.oauthId = :oauthId and a.active = 1")
    Optional<Account> findActiveAccountByOauthId(@Param(value = "oauthId") String oauthId);

    @Query("select a from account a where a.avatar.id = :avatarId and a.active = 1")
    Optional<Account> findAccountIdByAvatarId(@Param(value = "avatarId") Long avatarId);

    @Query("select a from account a where a.oauthId = :oauthId and a.platform = :platform")
    Optional<Account> findAccountByOauthIdAndPlatform(@Param(value = "oauthId") String oauthId, @Param(value = "platform")Platform platform);


}
