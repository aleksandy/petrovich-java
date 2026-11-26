/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich.rules.yaml.snakeyamlengine;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

import com.github.aleksandy.petrovich.rules.yaml.YamlLoader;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

/**
 * Загрузчик моделей правил на SnakeYAML Engine.
 *
 * <p>YAML разбирается в стандартные коллекции, а затем преобразуется
 * в модели правил базовым методом {@link #toModel(Object, Class)}.
 *
 * <p>Эта библиотека используется Jackson 3 как бэкенд разбора YAML,
 * поэтому обычно доступна там, где подключён Jackson 3.
 */
public final class SnakeYamlEngineLoader extends YamlLoader {

    /**
     * Создаёт загрузчик для указанных ресурсов.
     *
     * @param rules ресурс с правилами склонения
     * @param gender ресурс с правилами определения пола
     */
    public SnakeYamlEngineLoader(URL rules, URL gender) {
        super(rules, gender);
    }

    @Override
    protected <T> T load(URL resource, Class<T> type) {
        try (InputStream stream = resource.openStream()) {
            return toModel(new Load(LoadSettings.builder().build()).loadFromInputStream(stream), type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
