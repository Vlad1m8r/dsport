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

## API Conventions
- JSON: camelCase
- Use Bean Validation on request DTOs.
- Provide consistent error format via @ControllerAdvice (e.g., {timestamp, path, code, message, details}).
- Pagination/sorting: Spring Pageable where relevant.

## Logging
- Use SLF4J (log.info/debug/warn/error).
- Do not log secrets/tokens or personal data.

## Контекст продукта
- Описание MVP и юзер флоу: см. `PRODUCT.md` в корне репозитория.
