package com.github.aleksandy.petrovich.rules.yaml.snakeyaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

import com.github.aleksandy.petrovich.rules.yaml.YamlLoader;
import org.yaml.snakeyaml.Yaml;

/**
 * Загрузчик моделей правил на SnakeYAML.
 *
 * <p>YAML разбирается в стандартные коллекции, а затем преобразуется
 * в модели правил базовым методом {@link #toModel(Object, Class)}.
 */
public final class SnakeYamlLoader extends YamlLoader {

    private final Yaml yaml;

    /**
     * Создаёт загрузчик для указанных ресурсов.
     *
     * @param rules ресурс с правилами склонения
     * @param gender ресурс с правилами определения пола
     */
    public SnakeYamlLoader(URL rules, URL gender) {
        super(rules, gender);

        this.yaml = new Yaml();
    }

    @Override
    protected <T> T load(URL resource, Class<T> type) {
        try (InputStream stream = resource.openStream()) {
            return toModel(this.yaml.load(stream), type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
