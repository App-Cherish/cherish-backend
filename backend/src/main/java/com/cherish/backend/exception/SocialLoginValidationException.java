package com.cherish.backend.exception;

import com.cherish.backend.exception.base.NotExistBaseException;

public class SocialLoginValidationException extends NotExistBaseException {

    public SocialLoginValidationException() {
        super("소셜로그인 검증에 실패하였습니다.");
    }

}
