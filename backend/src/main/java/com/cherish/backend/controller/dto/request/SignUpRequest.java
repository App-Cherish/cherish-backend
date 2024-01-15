package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    private String oauthId;
    private String name;
    private Platform platform;
    private LocalDate birth;
    private Gender gender;
    private String deviceId;
    private String deviceType;
    private String accessToken;

    public SignUpRequest(String oauthId, String name, Platform platform, LocalDate birth, Gender gender, String deviceId, String deviceType, String accessToken) {
        this.oauthId = oauthId;
        this.name = name;
        this.platform = platform;
        this.birth = birth;
        this.gender = gender;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.accessToken = accessToken;
    }
}
