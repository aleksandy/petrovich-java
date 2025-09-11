package com.github.aleksandy.petrovich;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

import java.util.List;

public class TestRules implements Rules {

    @Override public RuleSet lastName() {
        return new RuleSet(
            List.of(
                new Rule(
                    Gender.MALE,
                    List.of("в"),
                    List.of("а", "у", "а", "ым", "е")
                )
            ),
            List.of()
        );
    }

    @Override
    public RuleSet firstName() {
        return new RuleSet(
            List.of(
                new Rule(
                    Gender.MALE,
                    List.of("н"),
                    List.of("а", "у", "а", "ом", "е")
                )
            ),
            List.of()
        );
    }

    @Override
    public RuleSet middleName() {
        return new RuleSet(
            List.of(
                new Rule(
                    Gender.MALE,
                    List.of("ич"),
                    List.of("а", "у", "а", "ем", "е")
                )
            ),
            List.of()
        );
    }
}
