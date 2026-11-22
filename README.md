petrovich-java
================

Java-библиотека для склонения русских фамилий, имён и отчеств по падежам —
реализация Ruby-гема [Petrovich](https://github.com/petrovich/petrovich-ruby).

## Модули

| Модуль | Назначение |
|---|---|
| `petrovich-core` | склонение (`Petrovich`, `Names`, `Case`) и определение пола |
| `petrovich-rules` | агрегатор модулей правил |
| `petrovich-rules-api` | API правил (`Rules`, `Rule`, `RuleSet`, `GenderRules`) |
| `petrovich-rules-yaml` | загрузка правил из `rules.yml` / `gender.yml` (`YamlRules`); реализации на Jackson 2, Jackson 3, SnakeYAML и SnakeYAML Engine |
| `petrovich-parity-tests` | сквозные тесты, портированные из petrovich-ruby, и тесты паритета загрузчиков YAML; не публикуется |

Модули поставляются с дескрипторами JPMS: `com.github.aleksandy.petrovich`,
`com.github.aleksandy.petrovich.rules` и
`com.github.aleksandy.petrovich.rules.yaml`. Последний экспортирует подпакеты
с реализациями разбора YAML (`jackson2`, `jackson3`, `snakeyaml`, `snakeyamlengine`).

## Maven dependency

Для работы нужны `petrovich-core` (сам движок), `petrovich-rules-yaml`
(встроенные правила в classpath) и хотя бы одна из библиотек разбора YAML.
`petrovich-rules-yaml` объявляет все четыре библиотеки опциональными,
поэтому выбранную нужно подключить явно. Если подключено несколько,
загрузчик выбирается автоматически по приоритету: Jackson 3, SnakeYAML Engine,
Jackson 2, SnakeYAML. Например, для Jackson 2:

```xml
<dependency>
  <groupId>com.github.aleksandy</groupId>
  <artifactId>petrovich-core</artifactId>
  <version>${petrovich.version}</version>
</dependency>
<dependency>
  <groupId>com.github.aleksandy</groupId>
  <artifactId>petrovich-rules-yaml</artifactId>
  <version>${petrovich.version}</version>
</dependency>
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-yaml</artifactId>
  <version>2.23.0</version>
</dependency>
```

`petrovich-core` транзитивно подтягивает `petrovich-rules-api`.

## Использование

```java
import com.github.aleksandy.petrovich.Case;
import com.github.aleksandy.petrovich.Gender;
import com.github.aleksandy.petrovich.Names;
import com.github.aleksandy.petrovich.Petrovich;
import com.github.aleksandy.petrovich.rules.yaml.YamlRules;

Petrovich petrovich = new Petrovich(new YamlRules());

Names genitive = petrovich.inflectTo(
    new Names("Иванов", "Иван", "Иванович", null),
    Case.GENITIVE
);
System.out.println(genitive); // Иванова Ивана Ивановича

Gender gender = petrovich.gender(new Names("Склодовская-Кюри", null, null, null));
System.out.println(gender); // FEMALE
```

`Names` допускает любую комбинацию частей — достаточно одной непустой.
Пол можно задать явно (`Gender.MALE`, `Gender.FEMALE`, `Gender.ANDROGYNOUS`),
иначе он определяется по правилам; результат склонения содержит определённый
пол или `null`, если пол определить не удалось.

## Свои правила

```java
new YamlRules();                                     // rules.yml и gender.yml
new YamlRules("custom-rules.yml");                   // свои правила склонения
new YamlRules("custom-rules.yml", "custom-gender.yml");
new YamlRules("/custom-rules.yml");                  // ведущий '/' тоже допустим
```

Пути — ресурсы classpath, всегда **относительно корня classpath**; ведущий
`/` допускается и просто отбрасывается. Наличие обоих ресурсов проверяется
сразу в конструкторе (fail-fast), а содержимое читается лениво при первом
обращении к соответствующей группе правил. Если ресурс не найден,
выбрасывается `IllegalStateException`; если YAML некорректен, тип исключения
зависит от выбранного загрузчика.

Загрузить оба набора правил заранее можно явным вызовом:

```java
YamlRules rules = new YamlRules();
rules.load(); // разбирает rules.yml и gender.yml сразу
```

## Реализации разбора YAML

Модуль `petrovich-rules-yaml` содержит четыре независимые реализации:

| Пакет | Библиотека | Зависимость |
|---|---|---|
| `...rules.yaml.jackson2` | Jackson 2 | `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.23.0` |
| `...rules.yaml.jackson3` | Jackson 3 | `tools.jackson.dataformat:jackson-dataformat-yaml:3.3.0` |
| `...rules.yaml.snakeyaml` | SnakeYAML | `org.yaml:snakeyaml:2.7` |
| `...rules.yaml.snakeyamlengine` | SnakeYAML Engine | `org.snakeyaml:snakeyaml-engine:3.2` |

Все четыре зависимости объявлены опциональными. Конструкторы `new YamlRules(...)`
без явного загрузчика выбирают первую доступную библиотеку в порядке
приоритета: **Jackson 3 → SnakeYAML Engine → Jackson 2 → SnakeYAML**.
Если ни одна библиотека не найдена, выбрасывается `IllegalStateException`.
Jackson 3 сам использует SnakeYAML Engine как бэкенд разбора YAML, поэтому
там, где подключён Jackson 3, SnakeYAML Engine обычно уже есть транзитивно.

Чтобы выбрать реализацию явно, передайте соответствующий загрузчик
в публичный конструктор `YamlRules(YamlLoader)`:

```java
import java.net.URL;
import com.github.aleksandy.petrovich.rules.yaml.YamlRules;
import com.github.aleksandy.petrovich.rules.yaml.snakeyaml.SnakeYamlLoader;

URL rules = ...;
URL gender = ...;
YamlRules yamlRules = new YamlRules(new SnakeYamlLoader(rules, gender));
```

Классы `Jackson2YamlLoader`, `Jackson3YamlLoader` и `SnakeYamlEngineLoader`
используются аналогично. Логика ленивого кэширования и проверки ресурсов
общая для всех реализаций.

При использовании JPMS выбранная библиотека должна быть разрешена в графе
модулей: добавьте её в `requires` своего `module-info.java` или запускайте
приложение с `--add-modules`. Зависимости модуля объявлены как `requires static`,
поэтому сами по себе они в граф модулей не попадают.

## Формат `rules.yml`

```yaml
lastname:
  exceptions:
    - gender: androgynous
      test: [дюма, тома, дега]
      mods: [., ., ., ., .]
  suffixes:
    - gender: female
      test: [ска, цка]
      mods: [-ой, -ой, -ую, -ой, -ой]
    - gender: male
      test: [ов, ев, ин]
      mods: [а, у, а, ым, е]
```

- `test` — суффиксы для обычных правил; для `exceptions` — целые слова,
  с которыми часть имени сравнивается полностью. Список `test` не может быть пустым.
- `mods` — ровно пять окончаний: родительный, дательный, винительный,
  творительный, предложный падежи. Символ `-` удаляет один символ с конца имени,
  `.` ничего не меняет, остальные символы дописываются.
- `gender` — `male`, `female` или `androgynous`.
- `tags` хранятся для совместимости с upstream-форматом, но при выборе правила
  не используются.
- Порядок правил важен: побеждает первое подходящее (сначала `exceptions`,
  затем `suffixes`).

Некорректное правило (пустой `test` или не пять элементов в `mods`) отвергается
при загрузке.

## Формат `gender.yml`

```yaml
gender:
  firstname:
    exceptions:
      male: [иван, пётр]
    suffixes:
      male: [н, й]
      female: [а, я]
```

Пол определяется по исключениям (точное совпадение слова) и суффиксам;
суффиксы проверяются от самого длинного к короткому.

## Отличие от Ruby-реализации

Если часть составного (дефисного) имени не определяется правилами, она не
перекрывает уже определённый пол другой части. Поэтому `Склодовская-Кюри`
определяется как `FEMALE` (Ruby-гем вернул бы `:androgynous`).

## Сборка и тесты

Для сборки нужен JDK 17 или новее. `maven-toolchains-plugin` выбирает любой
установленный JDK из диапазона (`jdk.version`), а `--release 17` фиксирует
байткод на Java 17 независимо от версии JDK. Конкретный JDK можно задать
явно, например `-Djdk.version=21`.

```sh
./mvnw test
```

Тесты паритета с petrovich-ruby лежат в отдельном модуле `petrovich-parity-tests`
и запускаются вместе с остальными.

## Публикация в Maven Central

Публикация идёт через Central Portal (`central-publishing-maven-plugin`) в
профиле `release`. Профиль включается флагом `-DperformRelease=true` (или
`-Prelease`) и дополнительно подписывает артефакты GPG.

### Разовая настройка

1. Зарегистрировать и подтвердить namespace `com.github.aleksandy` в
   [Central Portal](https://central.sonatype.com/) (для `com.github.*` —
   через привязку GitHub-аккаунта).
2. Создать GPG-ключ и опубликовать публичную часть на keyserver
   (`keys.openpgp.org`).
3. Сгенерировать Portal token и прописать его в `~/.m2/settings.xml`:

   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>TOKEN_USERNAME</username>
         <password>TOKEN_PASSWORD</password>
       </server>
     </servers>
   </settings>
   ```

### Локальный релиз

```sh
# 1. Выставить релизную версию (без -SNAPSHOT)
./mvnw versions:set -DnewVersion=2.0.0

# 2. Собрать, подписать и загрузить в Central Portal
MAVEN_GPG_PASSPHRASE='...' ./mvnw -DperformRelease=true deploy

# 3. Либо сразу опубликовать после валидации
MAVEN_GPG_PASSPHRASE='...' ./mvnw -DperformRelease=true \
  -Dcentral.autoPublish=true -Dcentral.waitUntil=published deploy
```

Без `central.autoPublish=true` бандл загружается в Portal, а публикацию нужно
подтвердить вручную на странице Deployments. Парольная фраза GPG читается из
`MAVEN_GPG_PASSPHRASE`; можно также использовать gpg-agent.

### Релиз из GitHub Actions

Workflow `release.yml` запускается по тегу `v*` (например, `v2.0.0`). Версия в
`pom.xml` должна совпадать с тегом и не должна быть `-SNAPSHOT`. Нужны секреты:

| Секрет | Назначение |
|---|---|
| `CENTRAL_USERNAME` | username Portal token |
| `CENTRAL_PASSWORD` | password Portal token |
| `GPG_PRIVATE_KEY` | приватный ключ в armored-формате |
| `GPG_PASSPHRASE` | парольная фраза ключа |

Модуль `petrovich-parity-tests` в публикацию не попадает.
