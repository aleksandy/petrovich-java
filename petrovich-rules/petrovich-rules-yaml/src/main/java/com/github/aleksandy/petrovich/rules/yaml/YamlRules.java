package com.github.aleksandy.petrovich.rules.yaml;

import java.net.URL;
import java.util.function.Supplier;

import com.github.aleksandy.petrovich.rules.GenderRules;
import com.github.aleksandy.petrovich.rules.RuleSet;
import com.github.aleksandy.petrovich.rules.Rules;

/**
 * Правила склонения и определения пола, загружаемые из YAML-ресурсов classpath.
 *
 * <p>По умолчанию используются ресурсы {@code rules.yml} (склонение)
 * и {@code gender.yml} (определение пола). Пути задаются относительно корня
 * classpath; ведущий {@code /} допускается. Существование ресурсов проверяется
 * сразу в конструкторе, а содержимое читается лениво — при первом обращении
 * к соответствующей группе правил.
 *
 * <p>Класс не зависит от библиотеки разбора YAML: чтение моделей выполняет
 * {@link YamlLoader}. Класс неизменяем и потокобезопасен:
 * загруженные правила кэшируются.
 */
public class YamlRules implements Rules {

    private static final String DEFAULT_RULES_RESOURCE = "/rules.yml";
    private static final String DEFAULT_GENDER_RULES_RESOURCE = "/gender.yml";

    private final Supplier<RulesModel> rules;
    private final Supplier<GenderRules> genderRules;

    /**
     * Создаёт экземпляр с ресурсами по умолчанию: {@code rules.yml}
     * и {@code gender.yml}.
     */
    public YamlRules() {
        this(DEFAULT_RULES_RESOURCE, DEFAULT_GENDER_RULES_RESOURCE);
    }

    /**
     * Создаёт экземпляр с собственным ресурсом правил склонения
     * и ресурсом определения пола по умолчанию ({@code gender.yml}).
     *
     * @param resourcePath путь к YAML-ресурсу правил склонения относительно
     *                     корня classpath; ведущий {@code /} допускается
     * @throws NullPointerException если {@code resourcePath} равен {@code null}
     * @throws IllegalStateException если ресурс не найден в classpath
     */
    public YamlRules(String resourcePath) {
        this(resourcePath, DEFAULT_GENDER_RULES_RESOURCE);
    }

    /**
     * Создаёт экземпляр с собственными ресурсами правил склонения
     * и определения пола.
     *
     * @param resourcePath путь к YAML-ресурсу правил склонения относительно
     *                     корня classpath; ведущий {@code /} допускается
     * @param genderResourcePath путь к YAML-ресурсу правил определения пола
     *                           относительно корня classpath; ведущий {@code /}
     *                           допускается
     * @throws NullPointerException если любой из путей равен {@code null}
     * @throws IllegalStateException если любой из ресурсов не найден в classpath
     */
    public YamlRules(String resourcePath, String genderResourcePath) {
        URL rulesResource = resource(resourcePath, "Rules resource");
        URL genderResource = resource(genderResourcePath, "Gender rules resource");

        YamlLoader loader = new JacksonYamlLoader(rulesResource, genderResource);

        this.rules = new Lazy<>(loader::loadRules);
        this.genderRules = new Lazy<>(loader::loadGender);
    }

    @Override
    public RuleSet lastName() {
        return this.rules.get().lastName();
    }

    @Override
    public RuleSet firstName() {
        return this.rules.get().firstName();
    }

    @Override
    public RuleSet middleName() {
        return this.rules.get().middleName();
    }

    @Override
    public GenderRules gender() {
        return this.genderRules.get();
    }

    /**
     * Явно загружает оба набора правил — склонения и определения пола.
     *
     * <p>Обычно ресурсы загружаются лениво при первом обращении к правилам.
     * Этот метод позволяет выполнить загрузку заранее, например при старте
     * приложения. Повторные вызовы не перечитывают ресурсы.
     *
     * @throws IllegalStateException если ресурс не найден в classpath
     * @throws java.io.UncheckedIOException если YAML-ресурс не удалось
     *                                      прочитать или разобрать
     */
    public void load() {
        this.rules.get();
        this.genderRules.get();
    }

    private static URL resource(String path, String description) {
        if (path == null) {
            throw new NullPointerException(description + " path must not be null.");
        }

        // ClassLoader всегда ожидает путь от корня classpath, поэтому
        // ведущий '/' просто отбрасываем.
        URL resource = YamlRules.class.getClassLoader()
            .getResource(path.startsWith("/") ? path.substring(1) : path);
        if (resource == null) {
            throw new IllegalStateException(
                description + " '" + path + "' has not been found on the classpath."
            );
        }

        return resource;
    }

    private static final class Lazy<T> implements Supplier<T> {

        private volatile Object value;
        private Supplier<? extends T> delegate;

        private Lazy(Supplier<? extends T> delegate) {
            this.delegate = delegate;
        }

        @Override public T get() {
            if (this.value == null) {
                synchronized (this) {
                    if (this.value == null) {
                        this.value = this.delegate.get();
                        this.delegate = null;
                    }
                }
            }

            return (T) this.value;
        }

    }

}
