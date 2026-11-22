package com.github.aleksandy.petrovich.rules.yaml;

import java.net.URL;

/**
 * Загрузчик моделей правил из YAML-ресурсов.
 *
 * <p>Хранит ресурсы и знает, какие модели из них читать. Вся специфика
 * конкретной библиотеки разбора YAML остаётся в реализации метода
 * {@link #load(URL, Class)}.
 *
 * <p>Класс не является частью публичного API: модели правил — внутренняя
 * деталь модуля.
 */
abstract class YamlLoader {

    private final URL rules;
    private final URL gender;

    YamlLoader(URL rules, URL gender) {
        this.rules = rules;
        this.gender = gender;
    }

    /**
     * Загружает модель правил склонения.
     *
     * @return модель правил склонения
     * @throws java.io.UncheckedIOException если ресурс не удалось прочитать
     *                                      или разобрать
     */
    final RulesModel loadRules() {
        return load(this.rules, RulesModel.class);
    }

    /**
     * Загружает модель правил определения пола.
     *
     * @return модель правил определения пола
     * @throws java.io.UncheckedIOException если ресурс не удалось прочитать
     *                                      или разобрать
     */
    final GenderRulesModel loadGender() {
        return load(this.gender, GenderRulesModel.class);
    }

    /**
     * Читает модель указанного типа из ресурса.
     *
     * @param resource ресурс с YAML
     * @param type тип модели
     * @param <T> тип модели
     * @return загруженная модель
     * @throws java.io.UncheckedIOException если ресурс не удалось прочитать
     *                                      или разобрать
     */
    abstract <T> T load(URL resource, Class<T> type);

}
