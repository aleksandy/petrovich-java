package com.github.aleksandy.petrovich.rules;

import static com.github.aleksandy.petrovich.Utils.nullToEmpty;

import java.util.List;

public record RuleSet(List<Rule> suffixes, List<Rule> exceptions) {

    public RuleSet {
        suffixes = nullToEmpty(suffixes);
        exceptions = nullToEmpty(exceptions);
    }

}
