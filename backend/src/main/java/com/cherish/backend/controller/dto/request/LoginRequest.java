package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.Platform;
import com.cherish.backend.service.dto.CreateTokenDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String oauthId;
    private Platform platform;
    private String accessToken;
    private String deviceId;
    private String deviceType;

    public LoginRequest(String oauthId, Platform platform, String accessToken, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.accessToken = accessToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public CreateTokenDto toTokenDto(Long avatarId) {
        return new CreateTokenDto(deviceId, deviceType, avatarId);
    }
}
