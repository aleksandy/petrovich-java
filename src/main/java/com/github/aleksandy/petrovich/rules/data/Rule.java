package com.github.aleksandy.petrovich.rules.data;

import static com.github.aleksandy.petrovich.Utils.*;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.aleksandy.petrovich.Gender;

public record Rule(

    Gender gender,

    @JsonProperty("test")
    List<String> testSuffixes,

    @JsonProperty("mods")
    List<String> modSuffixes,

    Set<String> tags
) {

    public Rule {
        testSuffixes = nullToEmpty(testSuffixes);
        modSuffixes = nullToEmpty(modSuffixes);
        tags = nullToEmpty(tags);
    }

}
