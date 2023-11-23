package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String oauthId;
    private String platform;
    private String deviceId;
    private String deviceType;

    public LoginRequest(String oauthId, String platform, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
