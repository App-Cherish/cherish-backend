package com.cherish.backend.exception;

public class ExistOauthIdException extends RuntimeException{

    public ExistOauthIdException() {
        super("이미 존재하는 oauthId입니다.");
    }

}
