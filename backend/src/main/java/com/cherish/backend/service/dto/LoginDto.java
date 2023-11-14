package com.cherish.backend.service.dto;

import lombok.Getter;

@Getter
public class LoginDto {

    private String oauthId;

    private String deviceType;

    private String deviceId;

    public LoginDto(String oauthId, String deviceType, String deviceId) {
        this.oauthId = oauthId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }
}
