package com.cherish.backend.service;

import com.cherish.backend.controller.dto.request.TokenLoginRequest;
import com.cherish.backend.controller.dto.response.LoginResponse;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.exception.OverExpiredDateException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.dto.CreateTokenDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class SessionTokenServiceTest {

    Avatar avatar;
    SessionToken sessionToken;

    @Autowired
    SessionTokenService sessionTokenService;

    @Autowired
    SessionTokenRepository sessionTokenRepository;

    @Autowired
    AvatarRepository avatarRepository;

    @MockBean
    Clock clock;

    @BeforeEach
    public void init() {
        avatar = Avatar.of("name1", LocalDate.now(), Gender.MALE);
        avatarRepository.save(avatar);
        sessionToken = SessionToken.of("device1", "type1", avatar);
        sessionTokenRepository.save(sessionToken);
        given(clock.instant()).willReturn(Clock.systemDefaultZone().instant());
        given(clock.getZone()).willReturn(Clock.systemDefaultZone().getZone());

    }


    @Test
    @DisplayName("1.avatarId와 기기와 관련된 정보를 이용해 token을 정상적으로 생성한다.")
    public void createTokenTest() throws Exception {
        //given
        LoginResponse createTokenResponse = sessionTokenService.createToken(new CreateTokenDto("asd1234", "type1", avatar.getId()));
        //when
        Optional<SessionToken> findToken = sessionTokenRepository.findSessionTokenBySessionTokenValue(createTokenResponse.getTokenId());
        //then
        assertThat(findToken.get().getSessionTokenVaule()).isEqualTo(createTokenResponse.getTokenId());
    }

    @Test
    @DisplayName("2.tokenSessionValue를 이용해 토큰 로그인 시도 시에 토큰을 정상적으로 생성한다.")
    public void tokenLoginTest() throws Exception {
        //given
        LoginResponse response = sessionTokenService.tokenLogin(new TokenLoginRequest(sessionToken.getSessionTokenVaule()));
        //when
        Optional<SessionToken> findToken = sessionTokenRepository.findSessionTokenBySessionTokenValue(response.getTokenId());
        //then
        assertThat(findToken.get().getSessionTokenVaule()).isEqualTo(response.getTokenId());
    }

    @Test
    @DisplayName("3.tokenSessionValue를 통해  토큰 로그인 시도 시에 기존의 세션은 비활성화 된다.")
    public void tokenLoginDeActiveTest() throws Exception {
        //given
        LoginResponse response = sessionTokenService.tokenLogin(new TokenLoginRequest(sessionToken.getSessionTokenVaule()));
        //when
        //then
        assertThat(sessionToken.getActive()).isEqualTo(0);
        assertThat(response.getTokenId()).isNotEqualTo(sessionToken.getSessionTokenVaule());

    }

    @Test
    @DisplayName("4.로그인 한적 없는 deviceId로 토큰 세션 조회 요청을 한 경우 예외를 출력한다.")
    public void getTokenDeviceIdNull() throws Exception {
        //given
        //when
        //then
        assertThrows(IllegalStateException.class, () -> sessionTokenService.getTokenByDeviceId("asdasdasd"));
    }

    @Test
    @DisplayName("5.로그인 한적 있는 deviceId로 토큰 세션 발급 조회을 한 경우 토큰을 출력한다.")
    public void getTokenDeviceIdTest() throws Exception {
        //given
        //when
        SessionToken findToken = sessionTokenService.getTokenByDeviceId(sessionToken.getDeviceId());
        //then
        assertThat(findToken.getSessionTokenVaule()).isEqualTo(sessionToken.getSessionTokenVaule());
    }

    @Test
    @DisplayName("6.유효기간이 지난 토큰으로 토큰로그인을 요청한 경우 예외를 출력한다.")
    public void overExpiredTokenLoginTest() throws Exception {
        //given
        Clock fixedClock = Clock.fixed(ZonedDateTime.parse("2100-01-01T00:00:00.000+09:00[Asia/Seoul]").toInstant(), ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
        //when
        //then
        assertThrows(OverExpiredDateException.class, () -> sessionTokenService.tokenLogin(new TokenLoginRequest(sessionToken.getSessionTokenVaule())));
    }

    @Test
    @DisplayName("7.토큰을 비활성화 요청시 해당 토큰을 비활성화 하여야 한다.")
    public void deActiveTokenTest() throws Exception {
        //given
        //when
        sessionTokenService.deActiveToken(sessionToken.getSessionTokenVaule());
        //then
        sessionTokenRepository.findSessionTokenBySessionTokenValue(sessionToken.getSessionTokenVaule());
        assertThat(sessionToken.getActive()).isEqualTo(0);
    }


}


