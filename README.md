[![Maven Central](https://img.shields.io/maven-central/v/com.github.aleksandy/petrovich-core?label=Maven%20Central)](https://central.sonatype.com/artifact/com.github.aleksandy/petrovich-core)

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
2. Создать GPG-ключ и опубликовать публичную часть на keyserver — см.
   [GPG-ключ](#gpg-ключ).
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

4. Для релиза из GitHub Actions создать environment `release` и добавить в
   него секреты — см. [Секреты CI](#секреты-ci).

### GPG-ключ

Для подписи артефактов нужен GPG-ключ; на keyserver попадает только его
публичная часть.

Создать ключ (RSA 4096, срок действия, реальные имя и email):

```sh
gpg --full-generate-key
```

Полный отпечаток — это 40 hex-символов в строке `Key fingerprint = ...`:

```sh
gpg --fingerprint --keyid-format=long
```

Именно он подставляется вместо `<FULL_FINGERPRINT>` в командах ниже; пробелы
из вывода нужно убрать. Short/long key ID тоже принимаются, но fingerprint
однозначен.

Опубликовать публичную часть:

```sh
# основной вариант: без подтверждения email, ключ доступен сразу
gpg --keyserver keyserver.ubuntu.com --send-keys <FULL_FINGERPRINT>

# опционально: UID публикуется только после подтверждения email
gpg --keyserver hkps://keys.openpgp.org --send-keys <FULL_FINGERPRINT>
```

Проверить публикацию в чистом keyring:

```sh
GNUPGHOME=$(mktemp -d) gpg --keyserver keyserver.ubuntu.com \
  --recv-keys <FULL_FINGERPRINT>
```

Ключ должен быть действующим (не истёкшим и не отозванным) и уметь
подписывать. Если в связке несколько секретных ключей, GPG по умолчанию берёт
первый; нужный ключ можно указать явно:

```sh
MAVEN_GPG_PASSPHRASE='...' ./mvnw -DperformRelease=true \
  -Dgpg.keyname=<FULL_FINGERPRINT> deploy
```

### Секреты CI

Приватный ключ нельзя выгрузить из веб-интерфейса: keyserver отдаёт только
публичную часть, а GitHub умеет добавлять секреты, но не экспортировать их.
Ключ экспортируется локально из связки GPG, а затем переносится в GitHub.

Релизный job объявляет `environment: release`, поэтому все четыре секрета
хранятся как **environment secrets**: их видят только job'ы этого environment,
а не все workflow репозитория.

```sh
gpg --armor --export-secret-keys <FULL_FINGERPRINT> \
  > /tmp/petrovich-release-private.asc
```

Файл начинается со строки `-----BEGIN PGP PRIVATE KEY BLOCK-----` и остаётся
защищён парольной фразой — она понадобится в секрете `GPG_PASSPHRASE`.

Добавить секреты через веб-интерфейс GitHub:

1. Открыть
   `https://github.com/aleksandy/petrovich-java/settings/environments`
   (Settings → Environments) и создать environment `release`, если его ещё
   нет.
2. Открыть environment `release` и нажать **Add environment secret**.
3. Для `GPG_PRIVATE_KEY`: в поле **Name** — `GPG_PRIVATE_KEY`, в поле
   **Secret** — всё содержимое файла, включая строки
   `-----BEGIN PGP PRIVATE KEY BLOCK-----` и
   `-----END PGP PRIVATE KEY BLOCK-----`. Нажать **Add secret**.
4. Повторить для `GPG_PASSPHRASE` (парольная фраза ключа),
   `CENTRAL_USERNAME` и `CENTRAL_PASSWORD` (Portal token).
5. В **Deployment branches and tags** выбрать `Selected branches and tags`
   и добавить правило для тега `v*`, чтобы environment был доступен только
   релизным тегам. Маска — glob-шаблон в синтаксисе Ruby `File.fnmatch`,
   а не регулярное выражение; `*` не совпадает с `/`, а точка литеральна,
   поэтому `v.*` не подойдёт для тегов вида `v2.0.0` — нужен `v*`.
   Опционально включить **Required reviewers**, если публикация должна
   требовать ручного подтверждения.
6. Удалить локальную копию ключа:

   ```sh
   shred -u /tmp/petrovich-release-private.asc
   ```

То же самое из командной строки, если установлен GitHub CLI:

```sh
gh secret set GPG_PRIVATE_KEY --env release \
  < /tmp/petrovich-release-private.asc
gh secret set GPG_PASSPHRASE --env release
gh secret set CENTRAL_USERNAME --env release
gh secret set CENTRAL_PASSWORD --env release
```

Секреты нельзя посмотреть после сохранения — только перезаписать. Если
приватный ключ скомпрометирован, отзовите его, используя revocation certificate
из `~/.gnupg/openpgp-revocs.d/`, и замените секрет.

### Локальный релиз

```sh
# 1. Выставить релизную версию (без -SNAPSHOT)
./mvnw versions:set -DnewVersion=2.0.0 -DgenerateBackupPoms=false

# 2. Собрать, подписать и загрузить в Central Portal
MAVEN_GPG_PASSPHRASE='...' ./mvnw -DperformRelease=true \
  -Dgpg.keyname=<FULL_FINGERPRINT> deploy

# 3. Либо сразу опубликовать после валидации
MAVEN_GPG_PASSPHRASE='...' ./mvnw -DperformRelease=true \
  -Dgpg.keyname=<FULL_FINGERPRINT> \
  -Dcentral.autoPublish=true -Dcentral.waitUntil=published deploy
```

Без `central.autoPublish=true` бандл загружается в Portal, а публикацию нужно
подтвердить вручную на странице Deployments. Парольная фраза GPG читается из
`MAVEN_GPG_PASSPHRASE`; можно также использовать gpg-agent.

Локальный `deploy` — альтернатива CI, а не дополнение к нему: после него не
пушьте тег `vX.Y.Z`, иначе release-workflow запустится и попытается
опубликовать ту же версию повторно. Перед локальным релизом полезно прогнать
сухой прогон с `-DskipUpload=true`, чтобы проверить бандл без загрузки.

### Релиз из GitHub Actions

Workflow `release.yml` запускается по тегу `v*` (например, `v2.0.0`). Тег должен
быть семантической версией (`vMAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]`), а версия
в `pom.xml` — совпадать с ним и не быть `-SNAPSHOT`; иначе job падает до сборки.
Job объявляет `environment: release`, поэтому берёт секреты из этого environment:

| Секрет environment `release` | Назначение |
|---|---|
| `CENTRAL_USERNAME` | username Portal token |
| `CENTRAL_PASSWORD` | password Portal token |
| `GPG_PRIVATE_KEY` | приватный ключ в armored-формате |
| `GPG_PASSPHRASE` | парольная фраза ключа |

Порядок добавления секретов и настройки ограничений — в разделе
[Секреты CI](#секреты-ci).

Модуль `petrovich-parity-tests` в публикацию не попадает.

### Порядок выпуска

Перед первым релизом проверьте, что namespace подтверждён, environment `release`
создан с четырьмя секретами, публичный GPG-ключ опубликован на keyserver, а
рабочая копия чистая (приватный ключ из репозитория удалён).

1. Убедиться, что версия ещё не публиковалась (`404` — версия свободна):

   ```sh
   curl -sI https://repo1.maven.org/maven2/com/github/aleksandy/petrovich-core/2.0.0/ | head -1
   ```

2. На `master` выставить релизную версию, собрать и закоммитить:

   ```sh
   ./mvnw versions:set -DnewVersion=2.0.0 -DgenerateBackupPoms=false
   ./mvnw clean verify
   git commit -am "release 2.0.0"
   git push origin master
   ```

3. Дождаться зелёного workflow Build на коммите релиза.

4. Поставить аннотированный тег и запушить его:

   ```sh
   git tag -a v2.0.0 -m "petrovich-java 2.0.0"
   git push origin v2.0.0
   ```

5. Открыть в Actions запуск Release. Если у environment включены Required
   reviewers, подтвердить деплой. Workflow проверит SemVer тега и версию в
   POM, подпишет артефакты, загрузит их в Central Portal и опубликует
   (`autoPublish=true`).

6. Проверить артефакты: в Central Portal они видны сразу, на
   `repo1.maven.org` — после синхронизации (несколько минут).

7. Поднять версию до следующей SNAPSHOT:

   ```sh
   ./mvnw versions:set -DnewVersion=2.0.1-SNAPSHOT -DgenerateBackupPoms=false
   git commit -am "start 2.0.1-SNAPSHOT"
   git push origin master
   ```

Опубликованная версия неизменяема: повторно загрузить её нельзя. Если релиз
упал до публикации, исправьте причину, удалите тег
(`git tag -d v2.0.0 && git push origin :refs/tags/v2.0.0`) и повторите с шага 4.
Если версия уже опубликована — выпускайте следующую.

#### Одним пушем

Если релиз рутинный, шаги 2–4 можно совместить. Тег ставится на релизный
коммит до bump'а версии, поэтому workflow соберёт `2.0.0`, хотя в `master`
уже будет следующая SNAPSHOT:

```sh
./mvnw versions:set -DnewVersion=2.0.0 -DgenerateBackupPoms=false
git commit -am "release 2.0.0"
git tag -a v2.0.0 -m "petrovich-java 2.0.0"

./mvnw versions:set -DnewVersion=2.0.1-SNAPSHOT -DgenerateBackupPoms=false
git commit -am "start 2.0.1-SNAPSHOT"

git push --atomic origin master v2.0.0
```

`--atomic` либо обновит ветку и тег вместе, либо не обновит ничего. Workflow
берётся из тегированного коммита, а `actions/checkout` выгружает сам тег,
поэтому версия в POM будет релизной. Минус схемы: если релиз упадёт, тег
придётся переставить на новый коммит с релизной версией — в `master` уже
стоит следующая SNAPSHOT. Для первого релиза надёжнее пошаговый вариант.
