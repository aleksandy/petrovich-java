package com.github.aleksandy.petrovich.rules;

import java.util.List;

public record RuleSet(List<Rule> suffixes, List<Rule> exceptions) {

    public RuleSet {
        suffixes = suffixes == null ? List.of() : suffixes;
        exceptions = exceptions == null ? List.of() : exceptions;
    }

}
