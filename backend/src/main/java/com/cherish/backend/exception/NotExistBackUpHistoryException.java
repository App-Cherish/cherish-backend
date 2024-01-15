package com.cherish.backend.exception;

public class NotExistBackUpHistoryException extends RuntimeException{

    public NotExistBackUpHistoryException() {
        super("백업 기록이 존재하지 않습니다.");
    }
}
