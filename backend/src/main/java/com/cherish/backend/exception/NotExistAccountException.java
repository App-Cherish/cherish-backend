package com.cherish.backend.exception;

public class NotExistAccountException extends RuntimeException{

    public NotExistAccountException() {
        super("존재하지 않는 계정입니다. 회원가입을 페이지로 요청을 다시 보내주세요.");
    }
}
