package com.cherish.backend.exception;


public class ExistBackUpHistory extends RuntimeException {

    public ExistBackUpHistory() {
        super("기존에 백업 히스토리가 존재합니다.");
    }
}
