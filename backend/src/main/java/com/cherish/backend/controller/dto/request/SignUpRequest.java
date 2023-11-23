package com.cherish.backend.controller.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    private String oauthId;
    private String name;
    private String platform;
    private LocalDate birth;
    private String gender;
    private String deviceId;
    private String deviceType;

    public SignUpRequest(String oauthId, String name, String platform, LocalDate birth, String gender, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.name = name;
        this.platform = platform;
        this.birth = birth;
        this.gender = gender;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
