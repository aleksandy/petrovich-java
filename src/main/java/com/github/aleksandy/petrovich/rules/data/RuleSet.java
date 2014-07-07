package com.github.aleksandy.petrovich.rules.data;

import static com.github.aleksandy.petrovich.rules.data.Utils.*;
import static java.util.Collections.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleSet {

    @JsonProperty("exceptions")
    private List<Rule> exceptions = emptyList();

    @JsonProperty("suffixes")
    private List<Rule> suffixes = emptyList();

    public List<Rule> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Rule> exceptions) {
        this.exceptions = nullToEmpty(exceptions);
    }

    public List<Rule> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(List<Rule> suffixes) {
        this.suffixes = nullToEmpty(suffixes);
    }

}
