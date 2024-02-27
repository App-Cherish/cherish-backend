package com.cherish.backend.service;

import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.SignUpRequest;
import com.cherish.backend.domain.*;
import com.cherish.backend.exception.ExistOauthIdException;
import com.cherish.backend.exception.LeaveAccountStoreException;
import com.cherish.backend.exception.NotExistAccountException;
import com.cherish.backend.repositroy.AccountRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

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

    @Autowired
    SessionTokenRepository sessionTokenRepository;

    @MockBean
    Clock clock;

    @BeforeEach
    public void init() {
        given(clock.instant()).willReturn(Clock.systemDefaultZone().instant());
        given(clock.getZone()).willReturn(Clock.systemDefaultZone().getZone());
        oauthId = "testOauthId";
        avatar = Avatar.of("avatar1", LocalDate.now(), Gender.MALE);
        account = Account.of(oauthId, Platform.KAKAO, avatar, "-8lX2TVOZJQi_44d6AFNLZTVQibekxlICigKPXOaAAABjb1LLBno6jj-qNQmaA");
        accountRepository.save(account);
    }

    @Test
    @DisplayName("1.정상적인 요청이 넘어온 경우 회원가입이 성공한다.")
    public void signUpServiceTest() throws Exception {
        //given
        String oauthId = "testId";
        Long avatarId = accountService.signUp(new SignUpRequest(
                oauthId,
                "testName",
                Platform.KAKAO,
                LocalDate.now(),
                Gender.MALE,
                "deviceid1", "deviceType1", "asdasdasasdas"));
        //when
        Account findAccount = accountRepository.findActiveAccountByOauthId(oauthId).get();

        //then
        assertThat(findAccount.getAvatar().getId()).isEqualTo(avatarId);
    }

    @Test
    @DisplayName("2.정상적인 요청이 들어오고 이미 회원가입이 완료된 account가 존재할 경우 avatarId를 정상적으로 출력한다.")
    public void loginServiceSuccessTest() throws Exception {
        //given
        Long avatarId = accountService.oauthLogin(new LoginRequest(oauthId, Platform.KAKAO, "accessToken", "iphone1", "devicetype1"));
        Account findAccount = accountRepository.findActiveAccountByOauthId(oauthId).get();
        //when
        //then
        assertThat(avatarId).isEqualTo(findAccount.getAvatar().getId());
    }

    @Test
    @DisplayName("3.정상적인 요청이 들어왔으나 OauthID는 일치하나 플랫폼이 일치하지 않는 경우 예외를 출력한다.")
    public void loginServiceFailTest1() throws Exception {
        //given
        //when
        //then
        assertThrows(NotExistAccountException.class, () -> accountService.oauthLogin(new LoginRequest(oauthId, Platform.APPLE, "accessToken", "iphone1", "devicetype1")));
    }

    @Test
    @DisplayName("4.정상적인 요청이 들어왔으나 플랫폼은 일치하나 oauthID가 일치하지 않는 경우 예외를 출력한다.")
    public void loginServiceFailTest2() throws Exception {
        //given
        //when
        //then
        assertThrows(NotExistAccountException.class, () -> accountService.oauthLogin(new LoginRequest("asdasdasdas", Platform.APPLE, "accessToken", "iphone1", "devicetype1")));

    }

    @Test
    @DisplayName("5.회원탈퇴 이후 일주일이 지나지 않은 경우 로그인을 시도할 경우 회원탈퇴 계정을 복구하는 예외를 출력한다.")
    public void loginServiceRedirectTest() throws Exception {
        //given
        accountService.leave(avatar.getId());
        //when
        //then
        assertThrows(LeaveAccountStoreException.class, () -> accountService.oauthLogin(new LoginRequest(oauthId, Platform.KAKAO, "accessToken", "iphone1", "devicetype1")));
    }

    @Test
    @DisplayName("6.회원탈퇴 이후 일주일이 지났을 때 로그인을 시도할 경우 계정이 존재하지 않는 예외를 출력한다..")
    public void loginServiceRedirectFailTest() throws Exception {
        //given
        accountService.leave(avatar.getId());
        //when
        Clock fixedClock = Clock.fixed(ZonedDateTime.now().plusDays(8).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
        //then
        assertThrows(NotExistAccountException.class, () -> accountService.oauthLogin(new LoginRequest(oauthId, Platform.APPLE, "accessToken", "iphone1", "devicetype1")));
    }


    @Test
    @DisplayName("7.이미 존재하는 oauthId로 회원가입 요청을 하는 경우 회원가입에 실패하고 예외를 출력한다.")
    public void signUpServiceExceptionTest() throws Exception {
        //given
        String oauthId = "testOauthId";
        //when
        //then
        assertThrows(ExistOauthIdException.class, () -> {
            accountService.signUp(new SignUpRequest(
                    oauthId,
                    "testName",
                    Platform.KAKAO,
                    LocalDate.now(),
                    Gender.MALE,
                    "deviceid1", "deviceType1", "asdasdasasdas"));
        });
    }


    @Test
    @DisplayName("8.회원탈퇴 시에 Account 엔티티 와 Avatar 엔티티를 비활성화 하여야 한다.")
    public void leaveSuccessTest1() throws Exception {
        //given
        accountService.leave(avatar.getId());
        //when
        //then
        assertThat(account.getActive()).isEqualTo(0);
        assertThat(avatar.getActive()).isEqualTo(0);
    }

    @Test
    @DisplayName("9.회원탈퇴 시에 SessionToken 엔티티 또한 모두 비활성화 하여야 한다.")
    public void leaveSuccessTest2() throws Exception {
        //given
        accountService.leave(avatar.getId());
        SessionToken.of("asdasd", "asdasdas", avatar);
        //when
        //then
        List<SessionToken> findTokens = sessionTokenRepository.findSessionTokenByAvatarId(avatar.getId());
        assertThat(findTokens.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("10.회원탈퇴 후 일주일 후에 다시 접속하였을 때 만약 account 엔티티가 활성화 되있으면 삭제 되어야 한다.")
    public void oauthLoginTest5() throws Exception {
        //given
        accountService.leave(avatar.getId());
        Clock fixedClock = Clock.fixed(ZonedDateTime.now().minusDays(8).toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
        //when
        //then
        assertThrows(NotExistAccountException.class, () -> accountService.oauthLogin(new LoginRequest(oauthId, Platform.KAKAO, "accessToken", "iphone1", "devicetype1")));
        Optional<Account> findAccount = accountRepository.findAccountByOauthId(oauthId);
        assertThat(findAccount.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("11.회원탈퇴가 되었어도 avatar 엔티티는 존재하지만 비활성화 상태이어야 한다.")
    public void leaveTest2() throws Exception {
        //given
        accountService.leave(avatar.getId());
        //when
        //then
        assertThat(avatar.getActive()).isEqualTo(0);
    }

    @Test
    @DisplayName("12.회원 복구시에는 oauthID에 해당하는 모든 엔티티가 활성화 되어야 한다.")
    public void activateTest1() throws Exception {
        //given
        accountService.leave(avatar.getId());
        accountService.activate(oauthId);
        //when
        //then
        Account findAccount = accountRepository.findAccountByOauthId(oauthId).get();
        assertThat(findAccount.getActive()).isEqualTo(1);
        assertThat(findAccount.getAvatar().getActive()).isEqualTo(1);
    }


}