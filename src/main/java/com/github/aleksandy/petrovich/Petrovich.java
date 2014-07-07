package com.github.aleksandy.petrovich;

import static java.util.Objects.*;

import com.github.aleksandy.petrovich.inflection.CaseInflection;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.loader.RulesLoader;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;
import com.google.common.base.Joiner;

public class Petrovich {

    private final RulesProvider provider;

    public Petrovich() {
        this(null);
    }

    public Petrovich(RulesLoader loader) {
        this.provider = new RulesProvider(
            loader == null
                ? new YamlRulesLoader()
                : loader
        );
    }

    public Names inflectTo(Names names, Case $case) {
        Gender gender =
            names.gender == null
                ? Gender.detect(names.middleName)
                : names.gender;

        CaseInflection inflection = new CaseInflection(provider, gender);

        return new Names(
            inflection.inflectLastNameTo(names.lastName, $case),
            inflection.inflectFirstNameTo(names.firstName, $case),
            names.middleName == null
                ? null
                : inflection.inflectMiddleNameTo(names.middleName, $case),
            gender
        );
    }

    public static class Names {
        public final String firstName;
        public final String lastName;
        public final String middleName;
        public final Gender gender;

        public Names(String lastName, String firstName, String middleName, Gender gender) {
            this.lastName = requireNonNull(lastName, "Last name was not provided");
            this.firstName = requireNonNull(firstName, "First name was not provided");
            this.middleName = middleName;
            this.gender = gender;
        }

        @Override
        public String toString() {
            return Joiner.on(' ')
                .skipNulls()
                .appendTo(
                    new StringBuilder(150),
                    lastName,
                    firstName,
                    middleName
                )
                .toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + firstName.hashCode();
            result = prime * result + lastName.hashCode();
            result = prime * result + (middleName == null ? 0 : middleName.hashCode());
            result = prime * result + gender.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Names) {
                Names other = (Names) obj;
                return firstName.equals(other.firstName)
                    && lastName.equals(other.lastName)
                    && (
                        middleName == null
                            ? other.middleName == null
                            : middleName.equals(other.middleName)
                    )
                    && gender == other.gender;
            }
            return false;
        }
    }
}

