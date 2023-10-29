package com.cherish.backend.service;

import com.cherish.backend.domain.Account;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.exception.ExistOauthIdException;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.dto.AnotherPlatformSignUpDto;
import com.cherish.backend.service.dto.LoginDto;
import com.cherish.backend.service.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AvatarRepository avatarRepository;
    private final SessionTokenRepository sessionTokenRepository;

    public Long oauthLogin(LoginDto loginDto) {

        Optional<Account> account = accountRepository.findAccountByOauthId(loginDto.getOauthId());

        if (account.isEmpty()) {
            if (!sessionTokenRepository.findExistTokenByDeviceId(loginDto.getDeviceId())){
                throw new NotExistAccountException();
            }
        }


        return account.get().getAvatar().getId();
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
    public Long anotherPlatformSignUp(AnotherPlatformSignUpDto signUpDto) {
        Optional<Avatar> findAvatar = avatarRepository.findAvatarById(signUpDto.getAvatarId());

        if (findAvatar.isEmpty()) {
            throw new IllegalArgumentException("avatarId가 잘못되었습니다.");
        }

        Account account = Account.of(signUpDto.getOauthId(), signUpDto.getPlatform(), findAvatar.get());

        accountRepository.save(account);

        return account.getAvatar().getId();
    }


}
