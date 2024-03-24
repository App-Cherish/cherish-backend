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

    @NotBlank
    private String accessToken;

    private String platform;

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String osVersion;
    @NotBlank
    private String deviceType;

    public KakaoLoginRequest(String oauthId, String accessToken, String platform, String refreshToken, String osVersion, String deviceType) {
        this.oauthId = oauthId;
        this.accessToken = accessToken;
        this.platform = platform;
        this.refreshToken = refreshToken;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
    }

    public CreateTokenDto toTokenDto(Long avatarId) {
        return new CreateTokenDto(osVersion, deviceType, avatarId);
    }


    public LoginDto toLoginDto() {
        return new LoginDto(this.oauthId, Platform.KAKAO, this.accessToken, this.refreshToken, this.osVersion, this.deviceType);
    }
}
