package com.cherish.backend.service;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.dto.TokenCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionTokenService {

    private final SessionTokenRepository tokenRepository;
    private final AvatarRepository avatarRepository;

    @Transactional
    public SessionToken createToken(Long avatarId, TokenCreateDto requestDto) {

        Optional<Avatar> findAvatar = avatarRepository.findAvatarById(avatarId);

        if (findAvatar.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 avatarId 입니다.");
        }

        return tokenRepository.save(SessionToken.of(
                requestDto.getDeviceId(),
                requestDto.getDeviceType(),
                findAvatar.get()));
    }

    @Transactional
    public SessionToken tokenLogin(String tokenSessionValue) {
        Optional<SessionToken> findToken = tokenRepository.findSessionTokenBySessionTokenValue(tokenSessionValue);

        if (findToken.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 토큰입니다.");
        }

        SessionToken token = findToken.get();
        token.deActive();

        return tokenRepository.save(SessionToken.of(
                token.getDeviceId(),
                token.getDeviceType(),
                token.getAvatar()));
    }


    public SessionToken getTokenByDeviceId(String deviceId) {
        Optional<SessionToken> findToken = tokenRepository.findSessionTokenByDeviceId(deviceId);

        if (findToken.isEmpty()) {
            throw new IllegalStateException("기존에 토큰을 발급한 적이 없습니다.");
        }

        return findToken.get();
    }


}
