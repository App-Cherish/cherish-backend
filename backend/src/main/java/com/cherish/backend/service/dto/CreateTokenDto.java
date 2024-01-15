package com.cherish.backend.service.dto;

import lombok.Getter;

@Getter
public class CreateTokenDto {

    private String deviceId;
    private String deviceType;
    private Long avatarId;

    public CreateTokenDto(String deviceId, String deviceType, Long avatarId) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.avatarId = avatarId;
    }
}
