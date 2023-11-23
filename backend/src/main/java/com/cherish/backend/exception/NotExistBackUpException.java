package com.cherish.backend.exception;

public class NotExistBackUpException  extends RuntimeException {
    public NotExistBackUpException() {
        super("존재하는 않는 백업 아이디 입니다.");
    }
}
