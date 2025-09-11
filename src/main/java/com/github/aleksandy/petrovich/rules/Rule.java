package com.github.aleksandy.petrovich.rules;

import static com.github.aleksandy.petrovich.Utils.*;

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
        testSuffixes = nullToEmpty(testSuffixes);
        modSuffixes = nullToEmpty(modSuffixes);
        tags = nullToEmpty(tags);
    }

}
