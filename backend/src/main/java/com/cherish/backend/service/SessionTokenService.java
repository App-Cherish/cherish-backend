package com.cherish.backend.service;

import com.cherish.backend.domain.Avatar;
import com.cherish.backend.domain.SessionToken;
import com.cherish.backend.repositroy.AvatarRepository;
import com.cherish.backend.repositroy.SessionTokenRepository;
import com.cherish.backend.service.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionTokenService {

    private final SessionTokenRepository tokenRepository;
    private final AvatarRepository avatarRepository;

    public SessionToken getToken(Long avatarId, TokenDto requestDto) {

        Optional<Avatar> findAvatar = avatarRepository.findAvatarById(avatarId);

        if (findAvatar.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 avatarId 입니다.");
        }
        
        return tokenRepository.save(SessionToken.of(
                requestDto.getDeviceId(),
                requestDto.getDeviceType(),
                findAvatar.get()));
    }


}
