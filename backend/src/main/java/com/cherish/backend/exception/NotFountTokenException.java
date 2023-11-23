package com.cherish.backend.exception;

public class NotFountTokenException extends RuntimeException {

    public NotFountTokenException() {
        super("존재하지 않는 토큰입니다..");
    }
}
