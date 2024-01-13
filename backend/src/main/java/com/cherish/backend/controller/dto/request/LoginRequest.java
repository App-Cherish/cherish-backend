package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String oauthId;
    private String platform;
    private String accessToken;
    private String deviceId;
    private String deviceType;

    public LoginRequest(String oauthId, String platform, String accessToken, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.accessToken = accessToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
