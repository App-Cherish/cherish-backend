package com.cherish.backend.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TokenDto {

    private String deviceId;

    private String deviceType;


    public TokenDto(String deviceId, String deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
