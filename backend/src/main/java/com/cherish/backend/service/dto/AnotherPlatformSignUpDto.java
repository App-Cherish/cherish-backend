package com.cherish.backend.service.dto;

import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AnotherPlatformSignUpDto {

    private Long avatarId;

    private String oauthId;

    private Platform platform;

    public AnotherPlatformSignUpDto(Long avatarId, String oauthId, Platform platform) {
        this.avatarId = avatarId;
        this.oauthId = oauthId;
        this.platform = platform;
    }
}
