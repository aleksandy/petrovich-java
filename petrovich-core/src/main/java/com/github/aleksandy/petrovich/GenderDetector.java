package com.github.aleksandy.petrovich;

import com.github.aleksandy.petrovich.rules.Gender;

public class GenderDetector {

    Gender detect(String middleName) {
        if (middleName == null || middleName.isBlank()) {
            throw new PetrovichException(
                "Parameter 'middleName' is null or blank. "
                    + "You must specify middle name to detect gender."
            );
        }

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

}
