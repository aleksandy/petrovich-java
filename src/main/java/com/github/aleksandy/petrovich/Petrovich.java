package com.github.aleksandy.petrovich;

import static java.util.Objects.*;

import com.github.aleksandy.petrovich.rules.data.Rules;

import java.util.Objects;
import java.util.StringJoiner;

public class Petrovich {

    private final Rules rules;

    public Petrovich(Rules rules) {
        this.rules = requireNonNull(rules, "Rules must not be null.");
    }

    public Names inflectTo(Names names, Case $case) {
        Gender gender =
            names.gender == null
                ? Gender.detect(names.middleName)
                : names.gender;

        CaseInflection inflection = new CaseInflection(this.rules, gender);

        return new Names(
            inflection.inflectLastNameTo(names.lastName, $case),
            inflection.inflectFirstNameTo(names.firstName, $case),
            names.middleName == null
                ? null
                : inflection.inflectMiddleNameTo(names.middleName, $case),
            gender
        );
    }

    public record Names(String lastName, String firstName, String middleName, Gender gender) {

        public Names {
            lastName = requireNonNull(lastName, "Last name was not provided");
            firstName = requireNonNull(firstName, "First name was not provided");
        }

        @Override
        public String toString() {
            var sb = new StringJoiner(" ");
            if (this.lastName != null) {
                sb.add(this.lastName);
            }
            if (this.firstName != null) {
                sb.add(this.firstName);
                if (this.middleName != null) {
                    sb.add(this.middleName);
                }
            }

            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Names other) {
                return this.firstName.equals(other.firstName)
                    && this.lastName.equals(other.lastName)
                    && Objects.equals(this.middleName, other.middleName)
                    && this.gender == other.gender;
            }
            return false;
        }
    }
}

