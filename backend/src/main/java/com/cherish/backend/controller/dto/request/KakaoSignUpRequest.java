package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.Gender;
import com.cherish.backend.service.dto.SignUpDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class KakaoSignUpRequest {

    @NotBlank
    private String oauthId;
    @NotBlank
    private String name;

    @NotNull
    private LocalDate birth;

    private Gender gender;
    @NotBlank
    private String deviceId;
    @NotBlank
    private String deviceType;

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

    public KakaoSignUpRequest(String oauthId, String name, LocalDate birth, Gender gender, String deviceId, String deviceType, String accessToken, String refreshToken) {
        this.oauthId = oauthId;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public SignUpDto toSignUpDto() {
        return new SignUpDto(this.oauthId, this.name, this.birth, this.gender, this.deviceId, this.deviceType, this.accessToken, this.refreshToken);
    }
}
