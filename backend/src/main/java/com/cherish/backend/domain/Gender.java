package com.cherish.backend.domain;

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
}
