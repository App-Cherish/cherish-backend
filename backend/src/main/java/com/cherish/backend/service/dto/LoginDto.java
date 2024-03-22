package com.cherish.backend.service.dto;

import com.cherish.backend.domain.Platform;
import lombok.Getter;

@Getter
public class LoginDto {

    private String oauthId;

    private Platform platform;

    private String accessToken;

    private String refreshToken;

    private String deviceId;

    private String deviceType;

    public LoginDto(String oauthId, Platform platform, String accessToken, String refreshToken, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
