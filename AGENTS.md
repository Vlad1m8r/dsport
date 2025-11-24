# AGENTS Instructions

## 1. Project Context & MVP Scope
**Product:** Telegram Web App (TWA) Fitness Tracker.
**Goal:** A lightweight mobile web app opened via Telegram Bot to track gym workouts efficiently.

**Core MVP User Flow:**
1. **Auth:** Silent login via Telegram `initData` (no passwords).
2. **Library:** User views "System Exercises" or creates "Custom Exercises".
3. **Workout Session:**
   - User starts a workout (timestamped).
   - User adds an exercise to the workout.
   - User logs multiple sets for that exercise (Weight + Reps).
4. **History:** User views past workouts and stats.

**Data Hierarchy:**
`User` -> has many `Workouts` -> has many `WorkoutExercises` -> has many `WorkoutSets`.

## 2. Repository Conventions
- **Language:** Java 17+.
- **Framework:** Spring Boot 3.
- **Style:** Google Java Style.
- **Commits:** Descriptive, focused. **Названия и сообщения коммитов строго на русском языке.**
- **Testing:** Run `./mvnw test` before finishing. Prefer Unit tests (JUnit 5, Mockito).
- **Lombok:** Use extensively (`@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`).
- **Documentation:** Ensure every Controller/DTO has OpenAPI annotations (`@Operation`, `@Schema`).

## 3. Architecture Guidelines (Strict)
- **Layered Architecture:** `Controller` -> `Service` -> `Repository` -> `Database`.
- **DTO Pattern:** NEVER expose `@Entity` in Controllers. Always map `Entity` <-> `DTO` (use MapStruct or manual mapping).
- **Dependency Injection:** Use Constructor Injection (via `@RequiredArgsConstructor`), avoid `@Autowired` on fields.
- **Error Handling:** Use a global `@RestControllerAdvice` to return structured JSON errors.

## 4. Database & Migrations
- **Database:** PostgreSQL.
- **Naming:** Tables and columns in `snake_case`. Java fields in `camelCase`.
- **Migrations:** Use Flyway.
  - Place in `src/main/resources/db/migration`.
  - Naming: `V{VERSION}__{Description}.sql` (e.g., `V1__init_schema.sql`).
  - **Rule:** Never modify an existing migration file after it has been applied. Create a new version.
- **Auditing:** All tables must have `created_at` and `updated_at`.

## 5. Security & Data Integrity (Critical)
- **Authentication:**
  - Trust **only** the Telegram `initData` hash.
  - Validate the hash using the Bot Token on every sensitive request (or exchange it for a JWT).
  - Never accept a plain `userId` in a header as proof of identity.
- **Input Validation:**
  - Use `jakarta.validation` (`@Valid`, `@NotNull`, `@Min`) on all Request DTOs.
  - Example: Weight cannot be negative; Name cannot be empty.
- **Data Isolation:**
  - Ensure users can ONLY access/modify their own workouts. Always filter queries by `currentUserId`.
  - **System Exercises** are read-only for users. Users can only delete/edit their **Custom Exercises**.
- **Secrets:** Never hardcode tokens or keys in source code. Use `application.properties` / environment variables.