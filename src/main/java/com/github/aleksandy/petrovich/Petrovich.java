package com.github.aleksandy.petrovich;

import static java.util.Objects.*;

import com.github.aleksandy.petrovich.inflection.CaseInflection;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.loader.RulesLoader;
import com.github.aleksandy.petrovich.rules.loader.YamlRulesLoader;

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

        CaseInflection inflection = new CaseInflection(this.provider, gender);

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
            StringBuilder sb = new StringBuilder(150);
            if (this.lastName != null) {
                sb.append(this.lastName).append(' ');
            }
            if (this.firstName != null) {
                sb.append(this.firstName).append(' ');
            }
            if (this.middleName != null) {
                sb.append(this.middleName);
            }
            return sb.toString().trim();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.firstName.hashCode();
            result = prime * result + this.lastName.hashCode();
            result = prime * result + (this.middleName == null ? 0 : this.middleName.hashCode());
            result = prime * result + this.gender.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Names) {
                Names other = (Names) obj;
                return this.firstName.equals(other.firstName)
                    && this.lastName.equals(other.lastName)
                    && (
                        this.middleName == null
                            ? other.middleName == null
                            : this.middleName.equals(other.middleName)
                    )
                    && this.gender == other.gender;
            }
            return false;
        }
    }
}

