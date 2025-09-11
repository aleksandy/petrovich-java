package com.github.aleksandy.petrovich;

import java.util.Objects;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

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
