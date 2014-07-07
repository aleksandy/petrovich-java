package com.github.aleksandy.petrovich.rules.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rules {

    @JsonProperty("lastname")
    private RuleSet lastName;

    @JsonProperty("firstname")
    private RuleSet firstName;

    @JsonProperty("middlename")
    private RuleSet middleName;

    public RuleSet getLastName() {
        return lastName;
    }

    public void setLastName(RuleSet lastName) {
        this.lastName = lastName;
    }

    public RuleSet getFirstName() {
        return firstName;
    }

    public void setFirstName(RuleSet firstName) {
        this.firstName = firstName;
    }

    public RuleSet getMiddleName() {
        return middleName;
    }

    public void setMiddleName(RuleSet middleName) {
        this.middleName = middleName;
    }

}
