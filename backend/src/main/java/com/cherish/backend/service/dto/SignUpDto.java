package com.cherish.backend.service.dto;

import com.cherish.backend.domain.Gender;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignUpDto {

    private String oauthId;

    private String name;

    private LocalDate birth;

    private Gender gender;

    private String deviceId;

    private String deviceType;

    private String accessToken;

    private String refreshToken;

    public SignUpDto(String oauthId, String name, LocalDate birth, Gender gender, String deviceId, String deviceType, String accessToken, String refreshToken) {
        this.oauthId = oauthId;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
