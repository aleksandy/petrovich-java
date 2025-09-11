package com.github.aleksandy.petrovich.rules;

import java.util.List;
import java.util.Set;

import com.github.aleksandy.petrovich.Gender;

public record Rule(
    Gender gender,
    List<String> testSuffixes,
    List<String> modSuffixes,
    Set<String> tags
) {

    public Rule {
        testSuffixes = testSuffixes == null ? List.of() : testSuffixes;
        modSuffixes = modSuffixes == null ? List.of() : modSuffixes;
        tags = tags == null ? Set.of() : tags;
    }

}
