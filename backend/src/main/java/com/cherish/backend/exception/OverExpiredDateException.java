package com.cherish.backend.exception;

import com.cherish.backend.exception.base.ForbiddenAccessBaseException;

public class OverExpiredDateException extends ForbiddenAccessBaseException {

    public OverExpiredDateException() {
        super("유효기간이 지난 토큰입니다. oauthLogin을 시도해주세요.");
    }
}
