package com.cherish.backend.service.dto;

import lombok.Getter;


@Getter
public class TokenCreateDto {

    private String deviceId;

    private String deviceType;

    public TokenCreateDto(String deviceId, String deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
