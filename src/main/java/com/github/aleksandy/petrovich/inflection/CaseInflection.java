package com.github.aleksandy.petrovich.inflection;

import static com.github.aleksandy.petrovich.Gender.*;
import static java.lang.String.*;
import static java.lang.Math.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.Map.Entry;

import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.exception.CantApplyRuleException;
import com.github.aleksandy.petrovich.exception.PetrovichException;
import com.github.aleksandy.petrovich.exception.UnknownCaseException;
import com.github.aleksandy.petrovich.exception.UnknownRuleException;
import com.github.aleksandy.petrovich.rules.RulesProvider;
import com.github.aleksandy.petrovich.rules.data.Rule;
import com.github.aleksandy.petrovich.rules.data.RuleSet;

public class CaseInflection {

    private final RulesProvider provider;
    private final Gender gender;

    private final Comparator<CharRuleTuple> ruleSorter = new Comparator<CharRuleTuple>() {

        @Override
        public int compare(CharRuleTuple x, CharRuleTuple y) {
            int cmp = Integer.compare(y.chars.length(), x.chars.length());
            if (cmp != 0) {
                return cmp;
            }
            if (x.rule.getGender() != CaseInflection.this.gender) {
                return 1;
            }
            if (y.rule.getGender() != CaseInflection.this.gender) {
                return -1;
            }
            String xFirstSuffix = x.rule.getTestSuffixes().get(0);
            String yFirstSuffix = y.rule.getTestSuffixes().get(0);

            cmp = Integer.compare(yFirstSuffix.length(), xFirstSuffix.length());
            if (cmp != 0) {
                return cmp;
            }

            return xFirstSuffix.compareTo(yFirstSuffix);
        }

    };

    public CaseInflection(RulesProvider provider, Gender gender) {
        this.provider = provider;
        this.gender = gender;
    }

    public String inflectFirstNameTo(String firstName, Case $case) {
        return inflectTo(firstName, $case, this.provider.getRules().getFirstName());
    }

    public String inflectLastNameTo(String lastName, Case $case) {
        return inflectTo(lastName, $case, this.provider.getRules().getLastName());
    }

    public String inflectMiddleNameTo(String middleName, Case $case) {
        return inflectTo(middleName, $case, this.provider.getRules().getMiddleName());
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
                    Collections.singletonMap("first_word", firstWord)
                )
            );
        }

        StringBuilder sb = new StringBuilder(name.length() + 10);
        for (String res : result) {
            sb.append(res).append('-');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private String findAndApply(String name, Case $case, RuleSet ruleSet, Map<String, Boolean> features) {
        try {
            Rule rule = findFor(name, ruleSet, features);
            return apply(name, rule, $case);
        } catch (PetrovichException e) {
            return name;
        }
    }

    private String apply(String name, Rule rule, Case $case) throws CantApplyRuleException, UnknownCaseException {
        StringBuilder result = new StringBuilder(name.length() + 5).append(name);

        CharacterIterator itr = new StringCharacterIterator(
            modificatorFor($case, rule)
        );
        char $char = itr.current();
        while ($char != CharacterIterator.DONE) {
            switch ($char) {
                case '.' :
                    break;
                case '-' :
                    if (result.length() == 0) {
                        throw new CantApplyRuleException(
                            format("Can't apply rule for '%s' case %s", name, $case)
                        );
                    }
                    result.setLength(result.length() - 1);
                    break;
                default:
                    result.append($char);
            }
            $char = itr.next();
        }

        return result.toString();
    }

    private String modificatorFor(Case $case, Rule rule) throws UnknownCaseException {
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
                throw new UnknownCaseException(
                    String.format("Unknown grammatical case: %s", $case)
                );
        }
    }

    private Rule findFor(
        String name,
        RuleSet ruleSet,
        Map<String, Boolean> features
    ) throws UnknownRuleException {
        Set<String> tags = extractTags(features);

        if (!ruleSet.getExceptions().isEmpty()) {
            try {
                return find(name, ruleSet.getExceptions(), true, tags);
            } catch (UnknownRuleException e) {
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
    ) throws UnknownRuleException {
        String loweredName = name.toLowerCase();
        List<CharRuleTuple> matchedRules = new ArrayList<>();
        for (Rule rule : rules) {
            String chars = matchRule(loweredName, rule, tags, matchWholeWord);
            if (chars != null) {
                matchedRules.add(
                    new CharRuleTuple(chars, rule)
                );
            }
        }
        if (matchedRules.isEmpty()) {
            throw new UnknownRuleException(
                format(
                    "Can't find rule for %s", name
                )
            );
        }

        Collections.sort(matchedRules, this.ruleSorter);
        return matchedRules.get(0).rule;

//        return Iterables.find(rules, new Predicate<Rule>() {
//
//            @Override
//            public boolean apply(Rule rule) {
//
//                if (
//
//                    rule.getTags().removeAll(tags);
//                ) {
//                    Gender genderRule = Gender.valueOf(rule.getGender());
//                    if (
//                        (genderRule != Gender.male && CaseInflection.this.gender != Gender.female)
//                        && (genderRule != Gender.female && CaseInflection.this.gender == Gender.female)
//                    ) {
//                        if (matchWholeWord) {
//                            String test = loweredName;
//                            for (String chars : rule.getTestSuffixes()) {
//                                if (test.equals(chars)) {
//                                    return true;
//                                }
//                            }
//                        } else {
//                            for (String chars : rule.getTestSuffixes()) {
//                                String test = loweredName.substring(
//                                    max(0, loweredNameLength - chars.length())
//                                );
//                                if (test.equals(chars)) {
//                                    return true;
//                                }
//                            }
//                        }
//                    }
//                }
//                return false;
//            }
//
//        }, null);
    }

    private String matchRule(String loweredName, Rule rule, Set<String> tags, boolean matchWholeWord) {
        Gender ruleGender = rule.getGender();
        if (
            !tagsAllow(tags, rule.getTags())
            || (
                // if any of values of gender is "androgynous" then rule can be matched
                (ruleGender == MALE && this.gender == FEMALE)
                || (ruleGender == FEMALE && this.gender != FEMALE)
            )
        ) {
            return null;
        }

        if (matchWholeWord) {
            for (String chars : rule.getTestSuffixes()) {
                if (loweredName.equals(chars)) {
                    return chars;
                }
            }
        } else {
            int loweredNameLength = loweredName.length();
            for (String chars : rule.getTestSuffixes()) {
                String test = loweredName.substring(
                    max(0, loweredNameLength - chars.length())
                );
                if (test.equals(chars)) {
                    return chars;
                }
            }
        }
        return null;
    }

    private boolean tagsAllow(Set<String> tags, Set<String> ruleTags) {
        Set<String> result = new HashSet<>(ruleTags);
        result.removeAll(tags);
        return result.isEmpty();
    }

    private static class CharRuleTuple {
        final String chars;
        final Rule rule;

        CharRuleTuple(String chars, Rule rule) {
            this.chars = chars;
            this.rule = rule;
        }
    }
}
