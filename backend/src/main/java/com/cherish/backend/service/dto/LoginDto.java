package com.cherish.backend.service.dto;

import com.cherish.backend.domain.Platform;
import lombok.Getter;

@Getter
public class LoginDto {

    private String oauthId;

    private String deviceType;

    private String deviceId;

    private Platform platform;

    public LoginDto(String oauthId, String deviceType, String deviceId, Platform platform) {
        this.oauthId = oauthId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.platform = platform;
    }
}
