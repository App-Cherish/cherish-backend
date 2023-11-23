package com.cherish.backend.service;

import com.cherish.backend.domain.Account;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.exception.*;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.dto.AnotherPlatformSignUpDto;
import com.cherish.backend.service.dto.LoginDto;
import com.cherish.backend.service.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AvatarRepository avatarRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final Clock clock;

    public Long oauthLogin(LoginDto loginDto) {

        Account account = accountRepository.findAccountByOauthIdAndPlatform(loginDto.getOauthId(), loginDto.getPlatform()).orElseThrow(NotExistAccountException::new);

        if (account.getLastModifiedDate().isAfter(LocalDateTime.now(clock).plusDays(7L))) {
            accountRepository.delete(account);
            throw new NotExistAccountException();
        }

        if (account.getActive() == 0 && account.getLastModifiedDate().isBefore(LocalDateTime.now(clock).plusDays(7L))) {
            throw new LeaveAccountStoreException();
        }

        return account.getAvatar().getId();


    }

    @Transactional
    public Long signUp(SignUpDto signUpDto) {

        Optional<Account> findAccount = accountRepository.findAccountByOauthId(signUpDto.getOauthId());

        if (!findAccount.isEmpty()) {
            throw new ExistOauthIdException();
        }

        Avatar avatar = Avatar.of(
                signUpDto.getName(),
                signUpDto.getBirth(),
                signUpDto.getGender()
        );

        Account account = Account.of(
                signUpDto.getOauthId(),
                signUpDto.getPlatform(),
                avatar
        );

        accountRepository.save(account);

        return account.getAvatar().getId();
    }

    @Transactional
    public void leave(Long avatarId) {
        Avatar findAvatar = avatarRepository.findAvatarById(avatarId).orElseThrow(NotExistAvatarException::new);
        Account findAccount = accountRepository.findAccountIdByAvatarId(avatarId).orElseThrow(NotExistAccountException::new);

        findAccount.deActive();
        findAvatar.deActive();

        sessionTokenRepository.findSessionTokenByAvatarId(avatarId).forEach(SessionToken::deActive);
    }






}
