package com.cherish.backend.service.dto;

import com.cherish.backend.domain.Platform;
import lombok.Getter;

@Getter
public class AnotherPlatformSignUpDto {

    private Long avatarId;

    private String oauthId;

    private Platform platform;

    private String deviceId;

    public AnotherPlatformSignUpDto(Long avatarId, String oauthId, Platform platform, String deviceId) {
        this.avatarId = avatarId;
        this.oauthId = oauthId;
        this.platform = platform;
        this.deviceId = deviceId;
    }
}
