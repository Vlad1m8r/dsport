# AGENTS Instructions
## Scope
These guidelines apply to the entire repository unless a nested `AGENTS.md` overrides them.
## About `AGENTS.md`
`AGENTS.md` files describe workflow expectations for contributors. They are scoped to their directory tree, so deeper files take
 precedence when instructions conflict. Treat them as living documentation and keep guidance concise and actionable.
## Repository Conventions
- Follow standard Java formatting (e.g., Google Java Style) when editing source under `src/`.
- Prefer descriptive commit messages and keep commits focused on a single concern.
- Пишите название коммитов и сообщения к коммитам на русском языке.
- Run `./mvnw test` before finishing substantial changes and include the command in your final report when executed.
- Document notable behavior changes in your summary and note any required follow-up work.
- Ensure every HTTP endpoint, controller method, and related DTO is documented with OpenAPI annotations (e.g., `@Operation`, `@ApiResponse`, `@Schema`) so the generated specification stays accurate.
- For collection responses, document arrays explicitly with `@ArraySchema` or omit `content` so springdoc infers the array from `List<>`.
- Используйте Lombok там, где это оправдано и помогает сократить шаблонный код.
- Отдавайте предпочтение модульным тестам; интеграционные тесты добавляйте только при острой необходимости.
- Для коллекций JPA избегайте одновременного fetch нескольких `List` (bag) в одной сущности: используйте `Set` либо `@OrderColumn`, чтобы предотвратить `MultipleBagFetchException`.
- Если коллекция стала `Set`, пересмотрите `equals/hashCode` сущности: не используйте только `id` для новых объектов без идентификатора, чтобы элементы не затирали друг друга до сохранения.
- Не меняйте `List`/`Set` без анализа жизненного цикла сущности и влияния на create/update: временные (transient) дочерние сущности с `id = null` не должны схлопываться.
- Не делайте `fetch`/`EntityGraph` сразу двух `List`-коллекций: используйте загрузку в два шага (граф + отдельный репозиторий) или batch fetching.

## Tech Stack (fixed)
- Java: 21
- Spring Boot: 3.x
- DB: PostgreSQL 16+
- Migrations: Flyway
- OpenAPI: springdoc-openapi
- Build: Maven Wrapper (./mvnw)

## Project Structure (preferred)
- controller/  (REST endpoints)
- dto/         (request/response)
- service/     (use-cases, business logic)
- repository/  (JPA repositories)
- domain/      (entities/value objects)
- mapper/      (MapStruct or manual mappers)
- exception/   (custom exceptions + handler)
- config/      (security, openapi, etc.)

## Database & Migrations (Flyway)
- Any schema change MUST be done via Flyway migration (no manual DB edits).
- Migration naming: VYYYYMMDD_HHMM__short_description.sql
- Migrations are append-only; never edit already applied migrations.

## Hibernate / JPA Collection Rules (Important)
- Do NOT change collection types (List/Set) in entities without checking:
 1) equals/hashCode strategy,
 2) persistence lifecycle (transient entities have null id),
 3) ordering requirements and API contract.
- Avoid Set for child entities with @EqualsAndHashCode based only on id, because new entities have null id and will collapse in Set.
  Use List + explicit business order field (orderIndex) + @OrderBy("orderIndex ASC") for ordered children.

## Fetching rules
- Do NOT use JOIN FETCH to fetch multiple List ("bag") collections in one query (causes MultipleBagFetchException).
- Prefer:
 - EntityGraph fetching only one collection level, and load deeper collections separately, or
 - batch fetching (@BatchSize / hibernate.default_batch_fetch_size).
- Any change to fetching strategy must keep existing use-cases working (createTemplate/startWorkout/etc.).

## Hibernate 6 Query Safety
### Общие запреты/рекомендации
- Не использовать enum-параметры и enum-литералы в JPQL/HQL (`@Query`) для сравнений вида `:param = Enum.VALUE`, в `CASE WHEN :param = ...`, а также в сложных `OR`-ветках с тем же параметром.
- Не привязывать JPQL к enum из `dto`-пакетов. Enum для API допускаются в DTO, но в запросах использовать безопасные строковые/простые параметры.

### Безопасные паттерны фильтров
- **String in repo:** передавать в репозиторий `enum.name()` как `String` и сравнивать со строковыми литералами (`'ALL'`, `'SYSTEM'`, `'MY'`).
- **Branching in service:** делать ветвление в сервисе (`ALL/SYSTEM/MY`) и вызывать разные методы репозитория без enum-параметра.
- **Criteria API / Specifications:** использовать для сложной динамической фильтрации и растущего числа опциональных параметров.

### Null/Optional параметры
- Для optional-параметров в JPQL ставить явную проверку `:param is null` первой в условии.
- Избегать выражений с неочевидным типом параметра (смешение типов в `CASE/COALESCE`, сравнения с литералами другого типа без явного приведения).

### Проверка перед мерджем (обязательно)
- Для каждого нового `@Query` с фильтрами проверить: вызов без параметров, вызов со всеми параметрами, и каждую ветку `scope/enum`-логики.
- Факт проверок фиксировать в PR summary.

### Памятка по симптомам
- При `Could not determine ValueMapping for SqmParameter(...)` сначала проверить `@Query` на enum-параметры/enum-литералы и типонечитаемые `OR/CASE`.
- Базовое исправление: перейти на `String in repo` или `Branching in service`.
- Не применять `lower()`/`upper()` к параметрам JPQL-фильтра; нормализовать строки (lowercase/trim) в сервисе, собирать шаблон (`%value%`) в сервисе и сравнивать через `lower(column) like :pattern` (без `concat`/`||` с nullable-параметрами).
- При ошибках `lower(bytea)` сначала проверить, что параметры запроса имеют корректные типы (`String`) и совпадение имён `@Param`, а также что запрос не вызывает `lower()` на параметре.

## API Conventions
- JSON: camelCase
- Use Bean Validation on request DTOs.
- Provide consistent error format via @ControllerAdvice (e.g., {timestamp, path, code, message, details}).
- Pagination/sorting: Spring Pageable where relevant.

## Logging
- Use SLF4J (log.info/debug/warn/error).
- Do not log secrets/tokens or personal data.

## Telegram Mini Apps auth (initData validation)
Источник истины для авторизации Mini App — официальная документация Telegram:
- https://core.telegram.org/bots/webapps (включая секцию **Validating data received via the Mini App**)
- https://core.telegram.org/api/bots/webapps (события/WebView, справочно)
- https://core.telegram.org/api (термины и разграничение Bot API vs Telegram API/MTProto, справочно)

### Источник аутентификации
- Backend получает `initData` как **raw query string** из заголовка `X-Tg-Init-Data`.
- Мы не доверяем `initDataUnsafe` и в целом никаким данным клиента без серверной криптографической проверки.
- Аутентификация пользователя в Mini App основана на проверке целостности `initData` по алгоритму Telegram.

### Алгоритм валидации initData (обязательно)
1. Распарсить `initData` как query string и извлечь `hash`.
2. Собрать `data-check-string`: пары `key=value`, отсортированные по ключу, разделённые символом `\n`, поле `hash` исключить.
3. Вычислить `secret_key = HMAC_SHA256(bot_token, "WebAppData")`.
4. Вычислить `expected_hash = hex(HMAC_SHA256(data-check-string, secret_key))`.
5. Сравнить `expected_hash` и `received hash` в постоянное время (constant-time compare).
6. Проверить `auth_date`: отклонять слишком старые данные (TTL задаётся конфигурацией backend).

### Edge cases и запреты
- Пустой/отсутствующий `X-Tg-Init-Data` для не-OPTIONS запросов -> ошибка авторизации.
- Отсутствующий `hash`, невалидный формат query string или некорректный URL-decoding -> `400 Bad Request`.
- Невалидная подпись (`hash` не совпал) -> `401 Unauthorized` или `403 Forbidden` по принятому security-контракту.
- Просроченный `auth_date` -> отдельная ошибка (например, `401` с кодом `AUTH_DATE_EXPIRED`).
- Запрещено "упрощать" проверку: нельзя доверять только `user.id`/`query_id`/другим полям без проверки подписи.
- Запрещено использовать значения из `initData` в бизнес-логике до успешной валидации.

### CORS / OPTIONS
- CORS preflight (`OPTIONS`) может приходить без `X-Tg-Init-Data`, это нормальное поведение браузера.
- Backend обязан пропускать `OPTIONS` без авторизации и без попытки валидации `initData`.
- Все реальные запросы (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) должны требовать валидный `initData`.

### Практические правила разработки
- Любые изменения заголовков, схемы авторизации и статусов ошибок фиксировать в `docs/` и/или `AGENTS.md` как контракт.
- Логи: не логировать `initData` целиком; при диагностике маскировать чувствительные части (`hash`, `user`, `auth_date`, etc.).
- Обработка ошибок должна быть консистентной:
  - `400` — невалидный формат/обязательные поля отсутствуют,
  - `401/403` — подпись невалидна,
  - отдельный код ошибки для `auth_date expired`.

### Scope: что НЕ делаем
- Для backend Mini App не реализуем MTProto-клиент и не используем `core.telegram.org/mtproto`.
- Для этого проекта релевантны Bot API и серверная валидация `initData`, а не Telegram API/MTProto авторизация.

## Security checklist
- [ ] `OPTIONS` разрешён без `X-Tg-Init-Data` и без auth-проверки.
- [ ] `initData` валидируется строго по алгоритму Telegram (data-check-string + HMAC).
- [ ] Проверяется свежесть `auth_date` (TTL/expiration).
- [ ] `initData` не логируется целиком (используется маскирование).

## Контекст продукта
- Описание MVP и юзер флоу: см. `PRODUCT.md` в корне репозитория.
- Описание проекта см. пакет `docs` в корне репозитория

- Если метод создаёт новую сущность и возвращает её DTO, необходимо гарантировать, что id уже сгенерирован (использовать saveAndFlush/flush или сохранять конкретную сущность), иначе DTO может содержать id=null при GenerationType.IDENTITY.
