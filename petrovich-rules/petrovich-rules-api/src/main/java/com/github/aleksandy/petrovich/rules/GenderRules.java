package com.github.aleksandy.petrovich.rules;

/**
 * Правила определения пола по частям имени.
 *
 * <p>Все методы по умолчанию возвращают пустой набор правил — реализация
 * может переопределить только нужные части.
 */
public interface GenderRules {

    /**
     * Возвращает правила определения пола по фамилии.
     *
     * @return правила для фамилий или пустой набор
     */
    default GenderRuleSet lastName() {
        return GenderRuleSet.EMPTY;
    }

    /**
     * Возвращает правила определения пола по имени.
     *
     * @return правила для имён или пустой набор
     */
    default GenderRuleSet firstName() {
        return GenderRuleSet.EMPTY;
    }

    /**
     * Возвращает правила определения пола по отчеству.
     *
     * @return правила для отчеств или пустой набор
     */
    default GenderRuleSet middleName() {
        return GenderRuleSet.EMPTY;
    }

}
