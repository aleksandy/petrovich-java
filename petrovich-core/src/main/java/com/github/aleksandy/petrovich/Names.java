package com.github.aleksandy.petrovich;

import com.github.aleksandy.petrovich.rules.Gender;

import java.util.StringJoiner;

/**
 * Имя человека: фамилия, имя, отчество и, возможно, явно заданный пол.
 *
 * <p>Любая из частей имени может быть {@code null}, но хотя бы одна должна
 * быть задана. Части обрезаются по краям, а пустые строки превращаются в {@code null}.
 *
 * @param lastName   Фамилия. Может быть {@code null}.
 * @param firstName  Имя. Может быть {@code null}.
 * @param middleName Отчество. Может быть {@code null}.
 * @param gender     Пол или {@code null}, если пол не задан явно, он будет определяться по частям имён.
 */
public record Names(String lastName, String firstName, String middleName, Gender gender) {

    /**
     * Создаёт имя, обрезая части и нормализуя пустые строки в {@code null}.
     *
     * @param lastName   Фамилия. Может быть {@code null}.
     * @param firstName  Имя. Может быть {@code null}.
     * @param middleName Отчество. Может быть {@code null}.
     * @param gender     Пол. Может быть {@code null}, если не задан явно, он будет определяться по частям имён.
     *
     * @throws IllegalArgumentException если все три части имени пусты или равны {@code null}
     */
    public Names {
        lastName = stripToNull(lastName);
        firstName = stripToNull(firstName);
        middleName = stripToNull(middleName);

        if (lastName == null && firstName == null && middleName == null) {
            throw new IllegalArgumentException(
                "At least one of lastName, firstName or middleName must be provided."
            );
        }
    }

    /**
     * @return Строковое представление без пустых частей в строгом порядке ФИО.
     */
    @Override
    public String toString() {
        var sb = new StringJoiner(" ");
        if (this.lastName != null) {
            sb.add(this.lastName);
        }
        if (this.firstName != null) {
            sb.add(this.firstName);
        }
        if (this.middleName != null) {
            sb.add(this.middleName);
        }

        return sb.toString();
    }

    private static String stripToNull(String v) {
        return v == null || (v = v.strip()).isEmpty() ? null : v;
    }

}
