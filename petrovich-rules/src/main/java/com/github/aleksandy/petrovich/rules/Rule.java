package com.github.aleksandy.petrovich.rules;

import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public record Rule(
    Gender gender,
    List<String> testSuffixes,
    List<String> modSuffixes,
    Set<String> tags
) {

    public Rule {
        requireNonNull(gender, "Gender must not be null.");
        testSuffixes = testSuffixes == null ? List.of() : testSuffixes;
        modSuffixes = modSuffixes == null ? List.of() : modSuffixes;
        tags = tags == null ? Set.of() : tags;
    }

    public Rule(Gender gender, List<String> testSuffixes, List<String> modSuffixes) {
        this(gender, testSuffixes, modSuffixes, null);
    }

}
