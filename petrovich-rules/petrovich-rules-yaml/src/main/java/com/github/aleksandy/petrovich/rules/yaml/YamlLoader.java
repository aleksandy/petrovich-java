/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich.rules.yaml;

import static java.util.stream.Collectors.toUnmodifiableMap;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.github.aleksandy.petrovich.rules.Gender;
import com.github.aleksandy.petrovich.rules.Rule;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.yaml.GenderRulesModel.GenderPartModel;

/**
 * Загрузчик моделей правил из YAML-ресурсов.
 *
 * <p>Хранит ресурсы и знает, какие модели из них читать. Вся специфика
 * конкретной библиотеки разбора YAML остаётся в реализации метода
 * {@link #load(URL, Class)}.
 *
 * <p>Это точка расширения: собственную реализацию можно передать
 * в {@link YamlRules#YamlRules(YamlLoader)}. Модели правил, которые
 * возвращает {@link #load(URL, Class)}, — внутренняя деталь модуля.
 */
public abstract class YamlLoader {

    private static final String LAST_NAME = "lastname";
    private static final String FIRST_NAME = "firstname";
    private static final String MIDDLE_NAME = "middlename";

    private static final String GENDER = "gender";
    private static final String EXCEPTIONS = "exceptions";
    private static final String SUFFIXES = "suffixes";
    private static final String TEST = "test";
    private static final String MODS = "mods";
    private static final String TAGS = "tags";

    private static final Map<String, Gender> GENDER_VALUES = Stream.of(Gender.values())
        .collect(toUnmodifiableMap(v -> v.name().toLowerCase(Locale.ROOT), Function.identity()));

    private final URL rules;
    private final URL gender;

    /**
     * Создаёт загрузчик для указанных ресурсов.
     *
     * @param rules ресурс с правилами склонения
     * @param gender ресурс с правилами определения пола
     */
    protected YamlLoader(URL rules, URL gender) {
        this.rules = rules;
        this.gender = gender;
    }

    /**
     * Загружает модель правил склонения.
     *
     * @return модель правил склонения
     * @throws RuntimeException если ресурс не удалось прочитать или разобрать;
     *                          конкретный тип зависит от реализации
     */
    public RulesModel loadRules() {
        return load(this.rules, RulesModel.class);
    }

    /**
     * Загружает модель правил определения пола.
     *
     * @return модель правил определения пола
     * @throws RuntimeException если ресурс не удалось прочитать или разобрать;
     *                          конкретный тип зависит от реализации
     */
    public GenderRulesModel loadGender() {
        return load(this.gender, GenderRulesModel.class);
    }

    /**
     * Читает модель указанного типа из ресурса.
     *
     * @param resource ресурс с YAML
     * @param type тип модели
     * @param <T> тип модели
     * @return загруженная модель
     * @throws RuntimeException если ресурс не удалось прочитать или разобрать;
     *                          конкретный тип зависит от реализации
     */
    protected abstract <T> T load(URL resource, Class<T> type);

    /**
     * Преобразует разобранный YAML в модель указанного типа.
     *
     * <p>Для загрузчиков, чья библиотека возвращает стандартные коллекции
     * ({@link Map}, {@link List}) вместо готовых моделей. Ожидает структуру
     * файлов {@code rules.yml} и {@code gender.yml}.
     *
     * @param root корень разобранного YAML; {@code null} трактуется как пустой документ
     * @param type тип модели
     * @param <T> тип модели
     * @return загруженная модель
     * @throws IllegalArgumentException если структура YAML не соответствует модели
     *                                  или тип модели не поддерживается
     */
    protected static <T> T toModel(Object root, Class<T> type) {
        if (type == RulesModel.class) {
            return type.cast(rulesModel(root));
        }
        if (type == GenderRulesModel.class) {
            return type.cast(genderRulesModel(root));
        }

        throw new IllegalArgumentException("Unsupported model type: " + type.getName());
    }

    private static RulesModel rulesModel(Object node) {
        Map<String, Object> root = asMap(node);

        return new RulesModel(
            ruleSet(root.get(LAST_NAME)),
            ruleSet(root.get(FIRST_NAME)),
            ruleSet(root.get(MIDDLE_NAME))
        );
    }

    private static RuleSet ruleSet(Object node) {
        Map<String, Object> model = asMap(node);

        return new RuleSet(
            rules(model.get(SUFFIXES)),
            rules(model.get(EXCEPTIONS))
        );
    }

    private static List<Rule> rules(Object node) {
        List<Object> models = asList(node);
        List<Rule> rules = new ArrayList<>(models.size());

        for (Object model : models) {
            Map<String, Object> rule = asMap(model);
            rules.add(new Rule(
                gender(rule.get(GENDER)),
                strings(rule.get(TEST)),
                strings(rule.get(MODS)),
                new LinkedHashSet<>(strings(rule.get(TAGS)))
            ));
        }

        return rules;
    }

    private static GenderRulesModel genderRulesModel(Object node) {
        Map<String, Object> root = asMap(node);
        Map<String, Object> parts = asMap(root.get(GENDER));
        Map<String, GenderPartModel> model = new HashMap<>(parts.size());

        parts.forEach((name, part) -> model.put(name, genderPartModel(part)));

        return new GenderRulesModel(model);
    }

    private static GenderPartModel genderPartModel(Object node) {
        Map<String, Object> model = asMap(node);

        return new GenderPartModel(
            genderGroups(model.get(EXCEPTIONS)),
            genderGroups(model.get(SUFFIXES))
        );
    }

    private static Map<Gender, List<String>> genderGroups(Object node) {
        Map<String, Object> groups = asMap(node);
        Map<Gender, List<String>> result = new HashMap<>(groups.size());

        groups.forEach((gender, values) -> result.put(gender(gender), strings(values)));

        return result;
    }

    private static Gender gender(Object node) {
        String value = node == null ? null : node.toString();
        Gender result = value == null ? null : GENDER_VALUES.get(value);

        if (result != null) {
            return result;
        }

        throw new IllegalArgumentException(
            "Invalid value '" + value + "' for enum Gender. Valid values: "
                + GENDER_VALUES.keySet() + '.'
        );
    }

    private static List<String> strings(Object node) {
        return asList(node).stream().map(String::valueOf).toList();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object node) {
        if (node == null) {
            return Map.of();
        }
        if (node instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }

        throw new IllegalArgumentException("Expected a YAML mapping, but got: " + node);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object node) {
        if (node == null) {
            return List.of();
        }
        if (node instanceof List<?> list) {
            return (List<Object>) list;
        }

        throw new IllegalArgumentException("Expected a YAML sequence, but got: " + node);
    }

}
