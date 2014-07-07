package com.github.aleksandy.petrovich.inflection;

import static java.lang.Math.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.data.Rule;
import com.github.aleksandy.petrovich.rules.data.RuleSet;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class CaseInflection {

    private RulesProvider provider;
    private Gender gender;

    public CaseInflection(RulesProvider provider, Gender gender) {
        this.provider = provider;
        this.gender = gender;
    }

    public String inflectFirstNameTo(String firstName, Case $case) {
        return inflectTo(firstName, $case, provider.getRules().getFirstName());
    }

    public String inflectLastNameTo(String lastName, Case $case) {
        return inflectTo(lastName, $case, provider.getRules().getLastName());
    }

    public String inflectMiddleNameTo(String middleName, Case $case) {
        return inflectTo(middleName, $case, provider.getRules().getMiddleName());
    }

    private String inflectTo(String name, Case $case, RuleSet ruleSet) {
        String[] chunks = name.split("-");
        int chunksLength = chunks.length;
        List<String> result = new ArrayList<String>(chunksLength);
        for (int idx = 0; idx < chunksLength; idx++) {
            boolean firstWord = idx == 0 && chunksLength > 1;
            result.add(
                findAndApply(
                    chunks[idx],
                    $case,
                    ruleSet,
                    ImmutableMap.of("first_word", firstWord)
                )
            );
        }

        return Joiner.on("-")
            .appendTo(
                new StringBuilder(name.length() + 10),
                result
            ).toString();
    }

    private String findAndApply(String name, Case $case, RuleSet ruleSet, Map<String, Boolean> features) {
        Rule rule = findRulesFor(name, ruleSet, features);

        return rule == null ? name : apply(name, $case, rule);
    }

    private String apply(String name, Case $case, Rule rule) {
        StringBuilder result = new StringBuilder(name.length() + 5).append(name);
        CharacterIterator itr = new StringCharacterIterator(
            findCaseModificator($case, rule)
        );
        char $char = itr.current();
        while ($char != CharacterIterator.DONE) {
            switch ($char) {
                case '.' :
                    break;
                case '-' :
                    result.setLength(result.length() - 1);
                    break;
                default:
                    result.append($char);
            }
            $char = itr.next();
        }

        return result.toString();
    }

    private String findCaseModificator(Case $case, Rule rule) {
        switch ($case) {
            case NOMINATIVE :
                return ".";
            case GENITIVE :
            case DATIVE :
            case ACCUSATIVE :
            case INSTRUMENTAL :
            case PREPOSITIONAL :
                return rule.getModSuffixes().get($case.getSuffixesIndex());
            default:
                throw new UnsupportedOperationException(
                    String.format("Unknown grammatical case: %s", $case)
                );
        }
    }

    private Rule findRulesFor(String name, RuleSet ruleSet, Map<String, Boolean> features) {
        Set<String> tags = extractTags(features);

        if (ruleSet.getExceptions() != null) {
            Rule rule = find(name, ruleSet.getExceptions(), true, tags);
            if (rule != null) {
                return rule;
            }
        }

        return find(name, ruleSet.getSuffixes(), false, tags);
    }

    private Set<String> extractTags(Map<String, Boolean> features) {
        int size = features.size();
        Set<String> result = new HashSet<>(size, 1.0F);
        for (Entry<String, Boolean> entry : features.entrySet()) {
            if (entry.getValue()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private Rule find(
        String name,
        List<Rule> rules,
        final boolean matchWholeWord,
        final Set<String> tags
    ) {
        final String loweredName = name.toLowerCase();
        final int loweredNameLength = loweredName.length();

        return Iterables.find(rules, new Predicate<Rule>() {

            @Override
            public boolean apply(Rule rule) {
                if (
                    !Iterables.filter(rule.getTags(), new Predicate<String>() {

                        @Override
                        public boolean apply(String input) {
                            return !tags.contains(input);
                        }

                    }).iterator().hasNext()
                ) {
                    Gender genderRule = Gender.valueOf(rule.getGender());
                    if (
                        (genderRule == Gender.male && gender == Gender.male) ||
                        (genderRule == Gender.female && gender == Gender.female)
                    ) {
                        if (matchWholeWord) {
                            String test = loweredName;
                            for (String chars : rule.getTestSuffixes()) {
                                if (test.equals(chars)) {
                                    return true;
                                }
                            }
                        } else {
                            for (String chars : rule.getTestSuffixes()) {
                                String test = loweredName.substring(
                                    max(0, loweredNameLength - chars.length())
                                );
                                if (test.equals(chars)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

        }, null);
    }

}
