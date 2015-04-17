package com.github.aleksandy.petrovich.rules.data;

import static com.github.aleksandy.petrovich.Utils.*;
import static java.util.Collections.*;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.aleksandy.petrovich.Gender;

public class Rule {

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("test")
    private List<String> testSuffixes = emptyList();

    @JsonProperty("mods")
    private List<String> modSuffixes = emptyList();

    @JsonProperty("tags")
    private Set<String> tags = emptySet();

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getTestSuffixes() {
        return this.testSuffixes;
    }

    public void setTestSuffixes(List<String> testSuffixes) {
        this.testSuffixes = nullToEmpty(testSuffixes);
    }

    public List<String> getModSuffixes() {
        return this.modSuffixes;
    }

    public void setModSuffixes(List<String> modSuffixes) {
        this.modSuffixes = nullToEmpty(modSuffixes);
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = nullToEmpty(tags);
    }

}
