package com.github.aleksandy.petrovich;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.GenderRule;
import com.github.aleksandy.petrovich.rules.GenderRuleSet;
import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

import java.util.List;
import java.util.Map;

public class TestRules implements Rules {

    @Override public RuleSet lastName() {
        return new RuleSet(
            List.of(
                // Иванов
                new Rule(
                    Gender.MALE,
                    List.of("в"),
                    List.of("а", "у", "а", "ым", "е")
                ),
                // Мамин
                new Rule(
                    Gender.MALE,
                    List.of("н"),
                    List.of("а", "у", "а", "ым", "е")
                ),
                // Сибиряк
                new Rule(
                    Gender.MALE,
                    List.of("к"),
                    List.of("а", "у", "а", "ом", "е")
                ),
                // Склодовская
                new Rule(
                    Gender.FEMALE,
                    List.of("ая"),
                    List.of("--ой", "--ой", "--ую", "--ой", "--ой")
                ),
                // Иванова
                new Rule(
                    Gender.FEMALE,
                    List.of("а"),
                    List.of("-ой", "-ой", "-у", "-ой", "-ой")
                )
            ),
            List.of(
                // Несклоняемые фамилии: правило исключения проверяется по всему слову.
                new Rule(
                    Gender.ANDROGYNOUS,
                    List.of("кюри"),
                    List.of(".", ".", ".", ".", ".")
                ),
                new Rule(
                    Gender.ANDROGYNOUS,
                    List.of("череззаборногузадерищенко"),
                    List.of(".", ".", ".", ".", ".")
                )
            )
        );
    }

    @Override
    public RuleSet firstName() {
        return new RuleSet(
            List.of(
                // Иван
                new Rule(
                    Gender.MALE,
                    List.of("н"),
                    List.of("а", "у", "а", "ом", "е")
                ),
                // Дмитрий
                new Rule(
                    Gender.MALE,
                    List.of("ий"),
                    List.of("-я", "-ю", "-я", "-ем", "-и")
                ),
                // Марья
                new Rule(
                    Gender.FEMALE,
                    List.of("ья"),
                    List.of("-и", "-е", "-ю", "--ьей", "-е")
                ),
                // Мария
                new Rule(
                    Gender.FEMALE,
                    List.of("ия"),
                    List.of("-и", "-и", "-ю", "-ей", "-и")
                ),
                // Ольга
                new Rule(
                    Gender.FEMALE,
                    List.of("га"),
                    List.of("-и", "-е", "-у", "-ой", "-е")
                )
            )
        );
    }

    @Override
    public RuleSet middleName() {
        return new RuleSet(
            List.of(
                // Иванович
                new Rule(
                    Gender.MALE,
                    List.of("ич"),
                    List.of("а", "у", "а", "ем", "е")
                ),
                // Ивановна
                new Rule(
                    Gender.FEMALE,
                    List.of("на"),
                    List.of("-ы", "-е", "-у", "-ой", "-ой")
                )
            )
        );
    }

    @Override
    public GenderRules gender() {
        return new GenderRules() {
            @Override public GenderRuleSet lastName() {
                return new GenderRuleSet(
                    Map.of(),
                    List.of(
                        new GenderRule(Gender.FEMALE, "ова"),
                        new GenderRule(Gender.FEMALE, "ая"),
                        new GenderRule(Gender.MALE, "ов")
                    )
                );
            }

            @Override public GenderRuleSet firstName() {
                return new GenderRuleSet(
                    Map.of(),
                    List.of(
                        new GenderRule(Gender.MALE, "ий"),
                        new GenderRule(Gender.FEMALE, "ья"),
                        new GenderRule(Gender.FEMALE, "ия"),
                        new GenderRule(Gender.FEMALE, "га"),
                        new GenderRule(Gender.MALE, "н")
                    )
                );
            }

            @Override public GenderRuleSet middleName() {
                return new GenderRuleSet(
                    Map.of(),
                    List.of(
                        new GenderRule(Gender.MALE, "ич"),
                        new GenderRule(Gender.FEMALE, "на")
                    )
                );           }
        };
    }

}
