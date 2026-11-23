package com.github.aleksandy.petrovich.rules;

/**
 * Пол имени.
 */
public enum Gender {

    /**
     * Пол не определён: имя может принадлежать и мужчине, и женщине
     * (например, «Саша»).
     */
    ANDROGYNOUS,

    /**
     * Мужской пол.
     */
    MALE,

    /**
     * Женский пол.
     */
    FEMALE;

    /**
     * Проверяет, является ли пол мужским.
     *
     * @return {@code true} для {@link #MALE}
     */
    public boolean isMale() {
        return this == MALE;
    }

    /**
     * Проверяет, является ли пол женским.
     *
     * @return {@code true} для {@link #FEMALE}
     */
    public boolean isFemale() {
        return this == FEMALE;
    }

    /**
     * Проверяет, является ли пол андрогинным (неопределённым).
     *
     * @return {@code true} для {@link #ANDROGYNOUS}
     */
    public boolean isAndrogynous() {
        return this == ANDROGYNOUS;
    }

}
