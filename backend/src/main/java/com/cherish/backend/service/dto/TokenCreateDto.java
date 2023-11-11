package com.cherish.backend.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TokenCreateDto {

    private String deviceId;

    private String deviceType;

    public TokenCreateDto(String deviceId, String deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
