package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    private String oauthId;
    @NotBlank
    private String name;

    private Platform platform;

    @NotNull
    private LocalDate birth;

    private Gender gender;
    @NotBlank
    private String deviceId;
    @NotBlank
    private String deviceType;
    @NotBlank
    private String refreshToken;

    public SignUpRequest(String oauthId, String name, Platform platform, LocalDate birth, Gender gender, String deviceId, String deviceType, String refreshToken) {
        this.oauthId = oauthId;
        this.name = name;
        this.platform = platform;
        this.birth = birth;
        this.gender = gender;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.refreshToken = refreshToken;
    }
}
