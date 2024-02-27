package com.cherish.backend.controller.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class LoginResponse {

    private String tokenId;


    public LoginResponse(String tokenId, LocalDateTime expiredTime) {
        this.tokenId = tokenId;
    }
}
