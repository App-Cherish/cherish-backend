package com.cherish.backend.exception;

public class OverExpiredDateException extends RuntimeException{

    public OverExpiredDateException() {
        super("유효기간이 지난 토큰입니다. oauthLogin을 시도해주세요.");
    }
}
