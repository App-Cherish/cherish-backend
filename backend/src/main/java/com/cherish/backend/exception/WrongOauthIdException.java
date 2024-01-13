package com.cherish.backend.exception;

public class WrongOauthIdException extends RuntimeException{

    public WrongOauthIdException() {
        super("oauth Id의 검증이 실패하였습니다.");
    }

}
