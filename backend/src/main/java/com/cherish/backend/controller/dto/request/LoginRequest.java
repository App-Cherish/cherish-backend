package com.cherish.backend.controller.dto.request;

import com.cherish.backend.domain.Platform;
import com.cherish.backend.service.dto.CreateTokenDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    private String oauthId;
    private Platform platform;
    @NotBlank
    private String accessToken;
    @NotBlank
    private String deviceId;
    @NotBlank
    private String deviceType;

    public LoginRequest(String oauthId, Platform platform, String accessToken, String deviceId, String deviceType) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.accessToken = accessToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public CreateTokenDto toTokenDto(Long avatarId) {
        return new CreateTokenDto(deviceId, deviceType, avatarId);
    }
}
