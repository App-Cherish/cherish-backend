package com.cherish.backend.domain;

public enum Platform {

    KAKAO("kakao"),
    APPLE("apple");

    private String value;

    public String getValue() {
        return value;
    }

    Platform(String value) {
        this.value = value;
    }
}
