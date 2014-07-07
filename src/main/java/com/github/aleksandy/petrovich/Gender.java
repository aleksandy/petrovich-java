package com.github.aleksandy.petrovich;

import com.google.common.base.Preconditions;

public enum Gender {

    androgynous, male, female;

    public static Gender detect(String middleName) {
        Preconditions.checkArgument(
            !isNullOrWhiteSpace(middleName),
            "Parameter 'middleName' is null or blank. You must specify middle name to detect gender."
        );

        if (
            middleName.endsWith("ич")
            || middleName.endsWith("ыч")
        ) {
            return Gender.male;
        } else if (middleName.endsWith("на")) {
            return Gender.female;
        } else {
            return Gender.androgynous;
        }
    }

    private static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().isEmpty();
    }

}
