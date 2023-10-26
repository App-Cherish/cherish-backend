package com.cherish.backend.controller.dto.response;

import lombok.Getter;

@Getter
public class CommonErrorResponse {

    private final String code;
    private final String message;

    public CommonErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
