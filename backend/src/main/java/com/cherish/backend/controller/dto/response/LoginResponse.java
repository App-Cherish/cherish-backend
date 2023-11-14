package com.cherish.backend.controller.dto.response;

import com.cherish.backend.util.DateFormattingUtil;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class LoginResponse {

    private String tokenId;

    private String expiredTime;

    public LoginResponse(String tokenId, LocalDateTime expiredTime) {
        this.tokenId = tokenId;
        this.expiredTime = DateFormattingUtil.localDateTimeToString(expiredTime);
    }
}
