petrovich-java
================

Java-библиотека для склонения русских фамилий, имён и отчеств по падежам —
реализация Ruby-гема [Petrovich](https://github.com/petrovich/petrovich-ruby).

## Модули

| Модуль | Назначение |
|---|---|
| `petrovich-core` | склонение (`Petrovich`, `Names`, `Case`) и определение пола |
| `petrovich-rules` | API правил (`Rules`, `Rule`, `RuleSet`, `GenderRules`) |
| `petrovich-rules-yaml` | загрузка правил из `rules.yml` / `gender.yml` (`YamlRules`) |
| `petrovich-parity-tests` | сквозные тесты, портированные из petrovich-ruby; не публикуется |

Модули поставляются с дескрипторами JPMS: `com.github.aleksandy.petrovich`,
`com.github.aleksandy.petrovich.rules` и
`com.github.aleksandy.petrovich.rules.yaml`.

## Maven dependency

Для работы нужны `petrovich-core` (сам движок) и `petrovich-rules-yaml`
(встроенные правила в classpath):

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
```

`petrovich-core` транзитивно подтягивает `petrovich-rules`.

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
выбрасывается `IllegalStateException`; если YAML некорректен —
`UncheckedIOException` с исходной причиной.

Загрузить оба набора правил заранее можно явным вызовом:

```java
YamlRules rules = new YamlRules();
rules.load(); // разбирает rules.yml и gender.yml сразу
```

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

```sh
./mvnw test
```

Тесты паритета с petrovich-ruby лежат в отдельном модуле `petrovich-parity-tests`
и запускаются вместе с остальными.
