package com.github.aleksandy.petrovich.rules.yaml;

import com.github.aleksandy.petrovich.rules.RuleSet;

/**
 * Корневая модель файла правил склонения.
 *
 * <p>Не зависит от библиотеки разбора YAML: содержит только данные.
 *
 * @param lastName правила для фамилий
 * @param firstName правила для имён
 * @param middleName правила для отчеств
 */
public record RulesModel(RuleSet lastName, RuleSet firstName, RuleSet middleName) {
}
