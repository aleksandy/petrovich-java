/**
 * Загрузка правил склонения и определения пола из YAML-ресурсов classpath.
 */
module com.github.aleksandy.petrovich.rules.yaml {

    requires com.github.aleksandy.petrovich.rules;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;

    exports com.github.aleksandy.petrovich.rules.yaml;

    opens com.github.aleksandy.petrovich.rules.yaml to com.fasterxml.jackson.databind;

}
