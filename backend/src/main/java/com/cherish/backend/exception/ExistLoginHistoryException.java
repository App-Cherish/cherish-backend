package com.cherish.backend.exception;

public class ExistLoginHistoryException extends RuntimeException {

    public ExistLoginHistoryException() {
        super("이번 기기에서 로그인 히스토리가 존재합니다. 타 플랫폼 API로 요청을 다시 보내주세요.");
    }
}
