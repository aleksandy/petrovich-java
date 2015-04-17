package com.github.aleksandy.petrovich.rules.data;

import static com.github.aleksandy.petrovich.Utils.*;
import static java.util.Collections.*;

import java.util.List;

public class RuleSet {

    private List<Rule> exceptions = emptyList();

    private List<Rule> suffixes = emptyList();

    public List<Rule> getExceptions() {
        return this.exceptions;
    }

    public void setExceptions(List<Rule> exceptions) {
        this.exceptions = nullToEmpty(exceptions);
    }

    public List<Rule> getSuffixes() {
        return this.suffixes;
    }

    public void setSuffixes(List<Rule> suffixes) {
        this.suffixes = nullToEmpty(suffixes);
    }

}
