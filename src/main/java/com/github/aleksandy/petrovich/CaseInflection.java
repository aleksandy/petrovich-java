package com.github.aleksandy.petrovich;

import static com.github.aleksandy.petrovich.Gender.*;
import static java.lang.String.*;
import static java.util.Objects.requireNonNull;
import static java.lang.Math.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

class CaseInflection {

    private static final Locale RUSSIA = Locale.forLanguageTag("ru-RU");

    private final Rules rules;
    private final Gender gender;

    CaseInflection(Rules rules, Gender gender) {
        this.rules = rules;
        this.gender = gender;
    }

    String inflectFirstNameTo(String firstName, Case $case) {
        return inflectTo(firstName, $case, this.rules.firstName());
    }

    String inflectLastNameTo(String lastName, Case $case) {
        return inflectTo(lastName, $case, this.rules.lastName());
    }

    String inflectMiddleNameTo(String middleName, Case $case) {
        return inflectTo(middleName, $case, this.rules.middleName());
    }

    private String inflectTo(String name, Case $case, RuleSet ruleSet) {
        name = requireNonNull(name, "Name must not be null.");
        String[] chunks = normalizeName(name).split("-");
        int chunksLength = chunks.length;
        String[] result = new String[chunksLength];
        for (int idx = 0; idx < chunksLength; idx++) {
            Set<String> tags = idx == 0 && chunksLength > 1 ? Set.of("first_word") : Set.of();
            result[idx] = findAndApply(chunks[idx], $case, ruleSet, tags);
        }

        StringBuilder sb = new StringBuilder(name.length() + 10);
        for (String res : result) {
            sb.append(res).append('-');
        }
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        }
        String stringResult = sb.toString();
        return stringResult.isEmpty() ? name : stringResult;
    }

    static String normalizeName(String name) {
        int length = name.length();
        int start = 0;
        int stop = length - 1;
        while (start < length && name.charAt(start) == '-') {
            start++;
        }
        while (start < stop && name.charAt(stop) == '-') {
            stop--;
        }
        return start == length ? "" : name.substring(start, stop + 1);
    }

    private String findAndApply(String name, Case $case, RuleSet ruleSet, Set<String> features) {
        Rule rule = findFor(name, ruleSet, features);
        return apply(name, rule, $case);
    }

    private String apply(String name, Rule rule, Case $case) {
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
                    if (result.isEmpty()) {
                        throw new PetrovichException(format("Can't apply rule for '%s' case %s", name, $case));
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

    private String modificatorFor(Case $case, Rule rule) {
        return switch ($case) {
            case NOMINATIVE -> ".";
            case GENITIVE, DATIVE, ACCUSATIVE, INSTRUMENTAL, PREPOSITIONAL ->
                rule.modSuffixes().get($case.getSuffixesIndex());
        };
    }

    private Rule findFor(String name, RuleSet ruleSet, Set<String> tags) {
        if (!ruleSet.exceptions().isEmpty()) {
            var result = find(name, ruleSet.exceptions(), true, tags);
            if (result != null) {
                return result;
            }
        }

        var result = find(name, ruleSet.suffixes(), false, tags);
        if (result == null) {
            throw new PetrovichException(format("Inflection rule '%s' has not been found.", name));
        }

        return result;
    }

    private Rule find(
        String name,
        List<Rule> rules,
        final boolean matchWholeWord,
        final Set<String> tags
    ) {
        String loweredName = name.toLowerCase(RUSSIA);
        return rules.stream()
            .map(rule -> {
                String chars = matchRule(loweredName, rule, tags, matchWholeWord);
                return chars != null ? new CharRuleTuple(chars, rule) : null;
            })
            .filter(Objects::nonNull)
            .min((x, y) -> {
                int cmp = Integer.compare(y.chars.length(), x.chars.length());
                if (cmp != 0) {
                    return cmp;
                }
                if (x.rule.gender() != CaseInflection.this.gender) {
                    return 1;
                }
                if (y.rule.gender() != CaseInflection.this.gender) {
                    return -1;
                }
                String xFirstSuffix = x.rule.testSuffixes().getFirst();
                String yFirstSuffix = y.rule.testSuffixes().getFirst();

                cmp = Integer.compare(yFirstSuffix.length(), xFirstSuffix.length());
                if (cmp != 0) {
                    return cmp;
                }

                return xFirstSuffix.compareTo(yFirstSuffix);
            })
            .map(CharRuleTuple::rule)
            .orElse(null);
    }

    private String matchRule(String loweredName, Rule rule, Set<String> tags, boolean matchWholeWord) {
        Gender ruleGender = rule.gender();
        if (
            !tagsAllow(tags, rule.tags())
            || (
                // if any of values of gender is "androgynous" then rule can be matched
                (ruleGender == MALE && this.gender == FEMALE)
                || (ruleGender == FEMALE && this.gender != FEMALE)
            )
        ) {
            return null;
        }

        if (matchWholeWord) {
            for (String chars : rule.testSuffixes()) {
                if (loweredName.equals(chars)) {
                    return chars;
                }
            }
        } else {
            int loweredNameLength = loweredName.length();
            for (String chars : rule.testSuffixes()) {
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

    private record CharRuleTuple(String chars, Rule rule) {}
}
