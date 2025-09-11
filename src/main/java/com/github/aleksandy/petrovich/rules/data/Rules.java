package com.github.aleksandy.petrovich.rules.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Rules(

    @JsonProperty("lastname")
    RuleSet lastName,

    @JsonProperty("firstname")
    RuleSet firstName,

    @JsonProperty("middlename")
    RuleSet middleName
) {
}
