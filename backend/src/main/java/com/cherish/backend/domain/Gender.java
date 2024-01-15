package com.cherish.backend.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Gender {

    MALE("male"),
    FEMALE("female");

    String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Gender parsing(String inputValue) {
        return Stream.of(Gender.values())
                .filter(gender -> gender.getValue().equals(inputValue))
                .findFirst()
                .orElse(null);
    }
}
