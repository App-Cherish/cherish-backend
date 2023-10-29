package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnotherLoginRequest {

    private String oauthId;
    private String deviceId;
    private String deviceType;
    private String platform;

    public AnotherLoginRequest(String oauthId, String deviceId, String deviceType, String platform) {
        this.oauthId = oauthId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.platform = platform;
    }
}
