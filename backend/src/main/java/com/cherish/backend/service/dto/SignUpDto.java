package com.cherish.backend.service.dto;


import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SignUpDto {

    private String oauthId;

    private Platform platform;

    private String name;

    private LocalDate birth;

    private Gender gender;

    private String deviceId;

    public SignUpDto(String oauthId, Platform platform, String name, LocalDate birth, Gender gender, String deviceId) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.deviceId = deviceId;
    }
}
