package com.cherish.backend.exception;

public class NotFoundSessionException extends RuntimeException {

    public NotFoundSessionException() {
        super("존재하지 않는 세션 입니다");
    }

}
