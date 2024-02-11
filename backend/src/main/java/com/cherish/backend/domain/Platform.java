package com.cherish.backend.domain;

import com.cherish.backend.exception.EnumTypeConvertException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

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

    @JsonCreator
    public static Platform parsing(String inputValue) {
        return Stream.of(Platform.values())
                .filter(platform -> platform.getValue().equals(inputValue))
                .findFirst()
                .orElseThrow(EnumTypeConvertException::new);
    }
}
