package com.cherish.backend.service;

import com.cherish.backend.domain.Account;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.service.dto.LoginDto;
import com.cherish.backend.service.dto.SignUpDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AccountServiceTest {

    String oauthId;
    Avatar avatar;
    Account account;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void init() {
        oauthId = "testOauthId";
        avatar = Avatar.of("avatar1", LocalDate.now(), Gender.MALE);
        account = Account.of(oauthId, Platform.KAKAO, avatar);
        accountRepository.save(account);
    }

    @Test
    @DisplayName("정상적인 요청이 넘어온 경우 회원가입이 성공한다.")
    public void signUpServiceTest() throws Exception {
        //given
        String oauthId = "testId";
        Long avatarId = accountService.signUp(new SignUpDto(oauthId,
                Platform.KAKAO,
                "testName",
                LocalDate.now(),
                Gender.MALE));
        //when
        Account findAccount = accountRepository.findAccountByOauthId(oauthId).get();

        //then
        assertThat(findAccount.getAvatar().getId()).isEqualTo(avatarId);
    }

    @Test
    @DisplayName("이미 존재하는 oauthId로 로그인 요청을 하는 경우 회원가입에 실패하고 예외를 출력한다.")
    public void signUpServiceExceptionTest() throws Exception {
        //givenDataIntegrityViolationException
        String oauthId = "testOauthId";
        //when
        //then
        assertThrows(DataIntegrityViolationException.class, () -> {
            accountService.signUp(new SignUpDto(oauthId,
                    Platform.KAKAO,
                    "testName",
                    LocalDate.now(),
                    Gender.MALE));
        });
    }

    @Test
    @DisplayName("이미 카카오 계정으로 회원가입을 하여 avatar가 존재하는 경우 애플 계정으로 회원을 가능하도록 한다.")
    public void signUpServiceTestIfKakaoAccountExist() throws Exception {
        String testKakaoOauthId = "testKakaoId";

        accountService.signUp(new SignUpDto(oauthId,
                Platform.KAKAO,
                "testName",
                LocalDate.now(),
                Gender.MALE));

    }

    @Test
    @DisplayName("정상적인 요청이 들어오고 이미 회원가입이 완료된 account가 존재할 경우 avatarId를 정상적으로 출력한다.")
    public void loginServiceTest() throws Exception {
        //given
        LoginDto loginDto = new LoginDto(oauthId, "iphone15", UUID.randomUUID().toString());
        //when
        Long findAvatarId = accountService.login(loginDto);
        //then
        assertThat(avatar.getId()).isEqualTo(findAvatarId);
    }

    @Test
    @DisplayName("정상적인 요청이 들어왔으나 존재하지 않는 계정인 경우 NotExiestAccountException을 출력한다.")
    public void loginServiceNotExistAccountServiceTest() throws Exception {
        //given
        String testId = UUID.randomUUID().toString().split("-")[0];
        LoginDto loginDto = new LoginDto(testId, "iphone15", UUID.randomUUID().toString());
        //when
        //then
        assertThrows(NotExistAccountException.class, () -> accountService.login(loginDto));
    }





}