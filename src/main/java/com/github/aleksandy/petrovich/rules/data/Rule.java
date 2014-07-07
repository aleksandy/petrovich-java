package com.github.aleksandy.petrovich.rules.data;

import static com.github.aleksandy.petrovich.rules.data.Utils.*;
import static java.util.Collections.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rule {

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("test")
    private List<String> testSuffixes = emptyList();

    @JsonProperty("mods")
    private List<String> modSuffixes = emptyList();

    @JsonProperty("tags")
    private List<String> tags = emptyList();

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getTestSuffixes() {
        return testSuffixes;
    }

    public void setTestSuffixes(List<String> testSuffixes) {
        this.testSuffixes = nullToEmpty(testSuffixes);
    }

    public List<String> getModSuffixes() {
        return modSuffixes;
    }

    public void setModSuffixes(List<String> modSuffixes) {
        this.modSuffixes = nullToEmpty(modSuffixes);
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = nullToEmpty(tags);
    }

}
