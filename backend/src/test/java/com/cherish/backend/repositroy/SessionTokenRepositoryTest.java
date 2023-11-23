package com.cherish.backend.repositroy;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.SessionToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class SessionTokenRepositoryTest {

    @Autowired
    SessionTokenRepository sessionTokenRepository;

    @Autowired
    AvatarRepository avatarRepository;

    SessionToken sessionToken;

    Avatar avatar;

    String device1;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        device1 = "device1";
        sessionToken = SessionToken.of(device1, "type1", avatar);
        avatarRepository.save(avatar);
        sessionTokenRepository.save(sessionToken);
    }

    @Test
    @DisplayName("sessionTokenValue로 SessionTokenValue를 찾을 수 있어야 한다.")
    public void findSessionTokenBySessionTokenValueTest() throws Exception {
        //given
        //when
        SessionToken findSessionToken = sessionTokenRepository.findSessionTokenBySessionTokenValue(sessionToken.getSessionTokenVaule()).get();
        //then
        assertThat(findSessionToken.getId()).isEqualTo(sessionToken.getId());
    }

    @Test
    @DisplayName("존재하지 않는 세션 값을 조회하면 null을 출력한다.")
    public void findSessionTokenBySessionTokenValueNullTest() throws Exception {
        //given
        //when
        Optional<SessionToken> findSessionToken = sessionTokenRepository.findSessionTokenBySessionTokenValue(UUID.randomUUID().toString());
        //then
        assertThat(findSessionToken.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("비활성화된 세션 토큰을 조회시에 null을 출력한다.")
    public void findSessionTokenByDeactiveSessionTokenValueNullTest() throws Exception {
        //given
        sessionToken.deActive();
        //when
        //then
        Optional<SessionToken> findSessionToken = sessionTokenRepository.findSessionTokenBySessionTokenValue(UUID.randomUUID().toString());
        assertThat(findSessionToken.isEmpty()).isTrue();
    }


    @Test
    @DisplayName("deviceId로 sessiontoken을 조회할 시에 저장 되어 있는 값을 찾을 수 있어야 한다.")
    public void findSessionTokenByDeviceIdTest() throws Exception {
        //given
        //when
        Optional<SessionToken> findSessionToken = sessionTokenRepository.findSessionTokenByDeviceId(device1);
        //then
        assertThat(findSessionToken.get().getId()).isEqualTo(sessionToken.getId());
    }

    @Test
    @DisplayName("존재하지 않는 세션값을 기기 아이디로 조회 할 시에 Null을 출력한다.")
    public void findSessionTokenByDeviceIdNullTest() throws Exception {
        //given
        //when
        Optional<SessionToken> findSessionToken = sessionTokenRepository.findSessionTokenByDeviceId(UUID.randomUUID().toString());
        //then
        assertThat(findSessionToken.isEmpty()).isTrue();
    }
}