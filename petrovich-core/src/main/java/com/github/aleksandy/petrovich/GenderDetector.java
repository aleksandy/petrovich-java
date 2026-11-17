package com.github.aleksandy.petrovich;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.GenderRule;
import com.github.aleksandy.petrovich.rules.GenderRuleSet;
import com.github.aleksandy.petrovich.rules.GenderRules;

/**
 * Определяет пол по фамилии, имени и отчеству с помощью правил пола
 * (алгоритм гема petrovich для ruby).
 *
 * <p>Отличие от ruby: у составной (дефисной) части имени неопределённая
 * андрогинная часть не перекрывает уже определённый пол другой части —
 * например, «Склодовская-Кюри» определяется как женское имя, хотя «Кюри»
 * правилами не определяется.
 */
class GenderDetector {

    private final GenderRules rules;

    GenderDetector(GenderRules rules) {
        this.rules = requireNonNull(rules, "Gender rules must not be null.");
    }

    /**
     * Определяет пол по переданным именам.
     *
     * <p>Отчество имеет наивысший приоритет: если оно задано и не андрогинно,
     * возвращается его пол. Иначе, если имя и фамилия противоречат друг другу,
     * определённая часть побеждает андрогинную. Если все определённые части
     * согласны, возвращается их пол; иначе результат — {@code null}
     * (пол не определён).
     *
     * @param names имена для определения пола
     * @param rules правила, содержащие правила определения пола
     * @return определённый пол или {@code null}, если его не удалось определить
     */
    Gender detect(Names names) {
        requireNonNull(names, "Names must not be null.");

        Map<NamePart, Gender> genders = new EnumMap<>(NamePart.class);

        detectPart(genders, NamePart.LAST, names.lastName(), this.rules.lastName());
        detectPart(genders, NamePart.FIRST, names.firstName(), this.rules.firstName());
        detectPart(genders, NamePart.MIDDLE, names.middleName(), this.rules.middleName());

        Gender middleNameGender = genders.get(NamePart.MIDDLE);
        if (middleNameGender != null && middleNameGender != Gender.ANDROGYNOUS) {
            return middleNameGender;
        }

        Set<Gender> distinctGenders = new HashSet<>(genders.values());
        if (distinctGenders.size() > 1) {
            Gender firstNameGender = genders.get(NamePart.FIRST);
            Gender lastNameGender = genders.get(NamePart.LAST);

            if (firstNameGender != Gender.ANDROGYNOUS && lastNameGender == Gender.ANDROGYNOUS) {
                return firstNameGender;
            }

            if (lastNameGender != Gender.ANDROGYNOUS && firstNameGender == Gender.ANDROGYNOUS) {
                return lastNameGender;
            }
        }

        return distinctGenders.size() == 1 ? distinctGenders.iterator().next() : null;
    }

    private static void detectPart(
        Map<NamePart, Gender> genders,
        NamePart part,
        String name,
        GenderRuleSet rules
    ) {
        if (name == null) {
            return;
        }

        Gender gender = Gender.ANDROGYNOUS;
        for (String chunk : name.split("-")) {
            GenderRule rule = rules.find(chunk);
            if (rule != null && rule.gender() != Gender.ANDROGYNOUS) {
                // Неопределённая (андрогинная) часть не перекрывает уже
                // определённый пол другой части составного имени:
                // «Склодовская-Кюри» остаётся женской, хотя «Кюри»
                // правилами не определяется.
                gender = rule.gender();
            }
        }

        genders.put(part, gender);
    }

    private enum NamePart {
        LAST, FIRST, MIDDLE
    }

}
