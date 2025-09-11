package com.github.aleksandy.petrovich;

import static java.util.Objects.*;

import com.github.aleksandy.petrovich.rules.Rules;

public class Petrovich {

    private final Rules rules;

    public Petrovich(Rules rules) {
        this.rules = requireNonNull(rules, "Rules must not be null.");
    }

    public Names inflectTo(Names names, Case $case) {
        Gender gender =
            names.gender() == null
                ? Gender.detect(names.middleName())
                : names.gender();

        CaseInflection inflection = new CaseInflection(this.rules, gender);

        return new Names(
            inflection.inflectLastNameTo(names.lastName(), $case),
            inflection.inflectFirstNameTo(names.firstName(), $case),
            names.middleName() == null
                ? null
                : inflection.inflectMiddleNameTo(names.middleName(), $case),
            gender
        );
    }

}

