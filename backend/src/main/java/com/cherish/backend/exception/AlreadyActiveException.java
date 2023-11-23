package com.cherish.backend.exception;

public class AlreadyActiveException extends RuntimeException{

    public AlreadyActiveException() {
        super("이미 활성화 된 계정입니다.");
    }
}
