package com.cherish.backend.service.dto;


import com.cherish.backend.domain.Gender;
import com.cherish.backend.domain.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpDto {

    private String oauthId;

    private Platform platform;

    private String name;

    private LocalDate birth;

    private Gender gender;

    public SignUpDto(String oauthId, Platform platform, String name, LocalDate birth, Gender gender) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
    }
}
