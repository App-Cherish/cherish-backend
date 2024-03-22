package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.Platform;
import com.cherish.backend.service.dto.CreateTokenDto;
import com.cherish.backend.service.dto.LoginDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {

    @NotBlank
    private String oauthId;

    private Platform platform;

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String deviceId;
    @NotBlank
    private String deviceType;

    public KakaoLoginRequest(String oauthId, Platform platform, String accessToken, String refreshToken, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public CreateTokenDto toTokenDto(Long avatarId) {
        return new CreateTokenDto(deviceId, deviceType, avatarId);
    }


    public LoginDto toLoginDto() {
        return new LoginDto(this.oauthId, this.platform, this.accessToken, this.refreshToken, this.deviceId, this.deviceType);
    }
}
