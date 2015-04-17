package com.github.aleksandy.petrovich;

import static com.github.aleksandy.petrovich.Utils.*;
import static java.lang.String.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.aleksandy.petrovich.exception.UnknownCaseException;

public enum Gender {

    ANDROGYNOUS("androgynous"),
    MALE("male"),
    FEMALE("female");

    private final String value;

    private Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public static Gender detect(String middleName) {
        if (isNullOrWhiteSpace(middleName)) {
            throw new IllegalArgumentException(
                "Parameter 'middleName' is null or blank. "
                + "You must specify middle name to detect gender."
            );
        };

        if (
            middleName.endsWith("ич")
            || middleName.endsWith("ыч")
        ) {
            return Gender.MALE;
        } else if (middleName.endsWith("на")) {
            return Gender.FEMALE;
        } else {
            return Gender.ANDROGYNOUS;
        }
    }

    @JsonCreator
    public static Gender byValue(String value) throws UnknownCaseException {
        for (Gender gender : values()) {
            if (gender.getValue().equals(value)) {
                return gender;
            }
        }
        throw new UnknownCaseException(
            format("Incorrect gender value '%s'", value)
        );
    }

}
