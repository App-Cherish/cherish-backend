package com.cherish.backend.service;

import com.cherish.backend.controller.ConstValue;
import com.cherish.backend.controller.dto.request.LoginRequest;
import com.cherish.backend.controller.dto.request.TokenLoginRequest;
import com.cherish.backend.controller.dto.response.LoginResponse;
import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.exception.OverExpiredDateException;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.dto.CreateTokenDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionTokenService {

    private final SessionTokenRepository tokenRepository;
    private final AvatarRepository avatarRepository;
    private final HttpSession session;
    private final Clock clock;


    @Transactional
    public LoginResponse createToken(CreateTokenDto createTokenDto) {

        Avatar findAvatar = avatarRepository
                .findAvatarById(createTokenDto.getAvatarId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 avatarId 입니다."));

        SessionToken token = tokenRepository.save(SessionToken.of(
                createTokenDto.getDeviceId(),
                createTokenDto.getDeviceType(),
                findAvatar));

        extractedSession(session, findAvatar.getId());

        return new LoginResponse(token.getSessionTokenVaule(),token.getExpired_date());
    }

    @Transactional
    public LoginResponse tokenLogin(TokenLoginRequest tokenLoginRequest) {
        SessionToken findToken = tokenRepository
                .findSessionTokenBySessionTokenValue(tokenLoginRequest.getToken())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 토큰입니다."));


        if (findToken.getExpired_date().isBefore(LocalDateTime.now(clock))) {
            throw new OverExpiredDateException();
        }

        findToken.deActive();

        SessionToken renewToken = tokenRepository.save(SessionToken.renew(findToken));

        extractedSession(session,renewToken.getAvatar().getId());

        return new LoginResponse(renewToken.getSessionTokenVaule(),renewToken.getExpired_date());
    }

    @Transactional
    public void deActiveToken(String tokenSessionValue) {
        Optional<SessionToken> findToken = tokenRepository.findSessionTokenBySessionTokenValue(tokenSessionValue);

        if (findToken.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 토큰입니다.");
        }

        SessionToken token = findToken.get();
        token.deActive();

        session.invalidate();
    }

    public SessionToken getTokenByDeviceId(String deviceId) {
        return tokenRepository
                .findSessionTokenByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalStateException("기존에 토큰을 발급한 적이 없습니다."));
    }

    private void extractedSession(HttpSession session, Long avatarId) {
        session.setAttribute(ConstValue.sessionName, avatarId);
        session.setMaxInactiveInterval(3600);
    }

}
