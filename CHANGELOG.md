# Changelog

Все заметные изменения проекта документируются в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/ru/1.1.0/),
проект следует [Semantic Versioning](https://semver.org/lang/ru/).

## [2.0.1] — 2026-11-26

Релиз посвящён приведению в порядок лицензионных уведомлений.
Публичный API и поведение склонения не меняются.
Лицензия проекта остаётся MIT.

### Добавлено

- `NOTICE` с атрибуцией `petrovich-rules` и `petrovich-ruby` и полным
  текстом MIT для производных компонентов.
- `CONTRIBUTING.md` с условиями приёма вкладов под MIT.
- SPDX-заголовки `MIT` во все Java-исходники, `rules.yml` и `gender.yml`.
- `LICENSE` и `NOTICE` в `META-INF` бинарных и sources-артефактов.
- Проверка наличия `LICENSE` и `NOTICE` в CI.

### Изменено

- `LICENSE`: copyright обновлён до `2014-2026`, добавлена ссылка на `NOTICE`.
- `README.md`: добавлены разделы «Лицензия и происхождение» и дисклеймер о независимом порте.
- `README.md`: исправлена ссылка бейджа лицензии (`LICENSE.txt` → `LICENSE`).
- `README.md`: добавлены бейджи Maven Central и Build Status.
- `pom.xml`: добавлен `inceptionYear` и копирование лицензионных файлов в артефакты.

### Исправлено

- В опубликованных артефактах теперь присутствуют уведомления об авторских правах для правил и портированных тестов, как требует MIT.
- Версия `2.0.0` была опубликована без этих файлов; `2.0.1` исправляет это для последующих артефактов.

## [2.0.0] — 2026-11-24

Первый релиз модульной версии 2.x. Обновление ломающее: изменились
Maven-координаты, пакеты и публичный API.

### Добавлено

- Модульная структура Maven: `petrovich-core`, `petrovich-rules`
  (агрегатор), `petrovich-rules-api`, `petrovich-rules-yaml`,
  `petrovich-parity-tests`.
- JPMS-дескрипторы для модулей `com.github.aleksandy.petrovich`,
  `com.github.aleksandy.petrovich.rules` и
  `com.github.aleksandy.petrovich.rules.yaml`.
- Определение пола по фамилии, имени и отчеству: `gender.yml`,
  `GenderDetector`, `Petrovich.gender`.
- Загрузка правил из classpath через `YamlRules`: fail-fast проверка
  ресурсов, ленивый разбор и явный `load()`.
- Четыре реализации разбора YAML: Jackson 2, Jackson 3, SnakeYAML и
  SnakeYAML Engine. Загрузчик выбирается автоматически по приоритету:
  Jackson 3 → SnakeYAML Engine → Jackson 2 → SnakeYAML.
- Явный выбор загрузчика через `YamlRules(YamlLoader)`.
- Поддержка пользовательских `rules.yml` и `gender.yml`.
- Валидация правил при загрузке: непустой `test` и ровно пять `mods`.
- Модуль `petrovich-parity-tests` со сквозными тестами, портированными
  из `petrovich-ruby`, и тестами паритета загрузчиков YAML.
- Maven Wrapper, GitHub Actions и публикация в Maven Central через
  Central Portal.
- GPG-подпись артефактов и проверка SemVer-тегов в release workflow.

### Изменено

- Минимальная версия Java — 17; сборка идёт на JDK 17+ с `--release 17`.
- Публичный API переработан: `Petrovich`, `Names`, `Case`, `Gender`,
  `Rules`, `Rule`, `RuleSet`, `GenderRules`, `YamlRules`.
- Склонение вызывается через `Petrovich.inflectTo`, определение пола —
  через `Petrovich.gender`.
- `rules.yml` обновлён по upstream `petrovich-rules`; добавлен `gender.yml`.
- Тесты переведены на JUnit 6 и AssertJ.
- README переписан под модули, новые координаты, форматы правил и публикацию.

### Ломающие изменения

- Артефакт `com.github.aleksandy:petrovich` заменён модулями
  `petrovich-core`, `petrovich-rules-api` и `petrovich-rules-yaml`.
- Удалены или перемещены старые классы: `rules.data.*`, `RulesProvider`,
  `YamlRulesLoader`, `inflection.CaseInflection`, старые исключения и `Gender`.
- Старый API склонения заменён на `Petrovich` + `Names` + `Case`.
- YAML-парсер больше не входит в библиотеку: нужную реализацию нужно
  подключать явно.

### Исправлено

- Исправлено склонение составных (дефисных) имён.
- Исправлено поведение при неизвестном поле или неподходящем правиле.
- Упрощена обработка ошибок и удалены устаревшие исключения.
