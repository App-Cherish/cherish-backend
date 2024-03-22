package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Account;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AccountRepositoryTest {

    String oauthId = "testOauth1";
    Avatar avatar;
    Account account;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AvatarRepository avatarRepository;

    @BeforeEach
    public void init() {
        avatar = Avatar.builder()
                .name("user1")
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .build();
        account = Account.builder()
                .oauthId(oauthId)
                .platform(Platform.KAKAO)
                .avatar(avatar)
                .refreshToken("refreshtoken1")
                .build();

        accountRepository.save(account);
        avatarRepository.save(avatar);
    }


    @Test
    @DisplayName("만약 avatar 엔티티가 존재하지 않는 상태에서 저장을 진행할 경우 예외를 출력한다.")
    public void saveTestIfAvatarIsNull() {
        Account account = Account.builder()
                .oauthId("test1")
                .platform(Platform.KAKAO)
                .avatar(null).build();

        assertThrows(DataIntegrityViolationException.class, () -> accountRepository.save(account));
    }

    @Test
    @DisplayName("oauthId 값으로 account 조회시에 해당하는 값이 조회 하였던 oauthId와 일치 하여야 한다..")
    public void findAccountTest() {
        Optional<Account> account = accountRepository.findActiveAccountByOauthId(oauthId);
        assertThat(account.get().getOauthId()).isEqualTo(oauthId);
    }

    @Test
    @DisplayName("oauthId 값으로 account 조회 시에 해당하는 값이 존재하지 않으면 null을 출력해야 한다.")
    public void findAccountNullTest() {
        String mockId = "";
        Optional<Account> account = accountRepository.findDeActiveAccountByOauthId(mockId);

        assertThat(account.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("만약 이미 존재하는 oauthId로 회원가입을 요청한 경우 예외를 출력해야 한다.")
    public void saveAccountFailTest1() throws Exception {
        //given
        Avatar avatar = Avatar.builder()
                .name("user1")
                .birth(LocalDate.now())
                .gender(Gender.MALE)
                .build();
        Account account = Account.builder()
                .oauthId(oauthId)
                .platform(Platform.KAKAO)
                .avatar(avatar).build();
        //when
        //then
        assertThrows(DataIntegrityViolationException.class, () -> accountRepository.save(account));

    }

    @Test
    @DisplayName("이미 카카오 계정으로 account가 저장되어 되어있는 경우 애플 계정으로도 엔티티 저장이 가능하여야 한다.")
    public void saveAccountTestIfExistKakaoAccount() throws Exception {
        //given
        String appleOauthTestId = "testAppleOAuthId";
        Account appleAccount = Account.of(appleOauthTestId, Platform.APPLE, avatar,"asdasdasdasdasdasdasd");
        //when
        Account saveAppleAccount = accountRepository.save(appleAccount);
        //then
        assertThat(appleAccount.getId()).isEqualTo(saveAppleAccount.getId());
        assertThat(appleAccount.getAvatar().getId()).isEqualTo(account.getAvatar().getId());
    }

    @Test
    @DisplayName("이미 카카오 계정으로 account가 저장되어 되어있는 경우 애플 계정으로도 엔티티 저장이 가능하여야 한다.")
    public void saveAccountTestIfExistAppleAccount() throws Exception {
        //given
        Avatar avatar2 = Avatar.of("testAvatar1",LocalDate.now(),Gender.MALE);

        String appleOauthTestId = "testAppleOAuthId";
        Account appleAccount = Account.of(appleOauthTestId, Platform.APPLE, avatar2,"asdasdasdasdasdasd");
        accountRepository.save(appleAccount);
        String kakaoOauthTestId = "testKakaoOAuthId";
        Account kakaoAccount = Account.of(kakaoOauthTestId, Platform.KAKAO, avatar2,"asdasdasdasds");
        //when
        accountRepository.save(appleAccount);
        //then
        assertThat(kakaoAccount.getId()).isEqualTo(kakaoAccount.getId());
        assertThat(appleAccount.getAvatar().getId()).isEqualTo(kakaoAccount.getAvatar().getId());
    }

    @Test
    @DisplayName("비활성화된 Account 엔티티를 조회시에 엔티티값을 출력해야 한다.")
    public void deActiveAccountEntityReturnNULLTest() throws Exception {
        //given
        account.deActive();
        accountRepository.save(account);
        //when
        //then
        assertThat(accountRepository.findDeActiveAccountByOauthId(account.getOauthId()).isPresent()).isTrue();
    }




}