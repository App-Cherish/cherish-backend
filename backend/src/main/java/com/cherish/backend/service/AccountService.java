package com.cherish.backend.service;

import com.cherish.backend.domain.Account;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.AccountRepository;
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

    public Long login(LoginDto loginDto) {

        Optional<Account> account = accountRepository.findAccountByOauthId(loginDto.getOauthId());

        if (account.isEmpty()) {
            throw new NotExistAccountException();
        }

        return account.get().getAvatar().getId();
    }

    @Transactional
    public Long signUp(SignUpDto signUpDto) {

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


}
