/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

/**
 * Загрузка правил склонения и определения пола из YAML-ресурсов classpath.
 */
module com.github.aleksandy.petrovich.rules.yaml {

    requires com.github.aleksandy.petrovich.rules;

    requires static com.fasterxml.jackson.annotation;
    requires static com.fasterxml.jackson.core;
    requires static com.fasterxml.jackson.databind;
    requires static com.fasterxml.jackson.dataformat.yaml;
    requires static tools.jackson.core;
    requires static tools.jackson.databind;
    requires static tools.jackson.dataformat.yaml;
    requires static org.yaml.snakeyaml;
    requires static org.snakeyaml.engine;

    exports com.github.aleksandy.petrovich.rules.yaml;
    exports com.github.aleksandy.petrovich.rules.yaml.jackson2;
    exports com.github.aleksandy.petrovich.rules.yaml.jackson3;
    exports com.github.aleksandy.petrovich.rules.yaml.snakeyaml;
    exports com.github.aleksandy.petrovich.rules.yaml.snakeyamlengine;

    opens com.github.aleksandy.petrovich.rules.yaml to com.fasterxml.jackson.databind, tools.jackson.databind;
    opens com.github.aleksandy.petrovich.rules.yaml.jackson2 to com.fasterxml.jackson.databind;
    opens com.github.aleksandy.petrovich.rules.yaml.jackson3 to tools.jackson.databind;

}
