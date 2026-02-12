# Backend Changelog

- Добавлены endpoints exercise picker: GET /api/muscle-groups и GET /api/exercises с дефолтным scope=ALL,
  опциональными фильтрами query/muscleGroup/scope, сортировкой (system -> my, затем name) и summary DTO.
- Для exercise picker реализована загрузка muscle groups без N+1 через единый запрос (left join + агрегация в сервисе).
- OpenAPI для новых endpoint'ов и DTO описан через аннотации в коде; YAML-спецификация будет сгенерирована отдельно.
- Исправлена проекция времени для last-max (Instant вместо OffsetDateTime) и возвращаются нулевые значения
  maxWeight/maxDurationSeconds при отсутствии данных.
- Добавлен PATCH /api/workouts/{workoutId}/sets/{setEntryId}: частичное обновление подхода с проверкой ownership
  (принадлежность подхода тренировке и пользователю) и валидацией, что после обновления задано reps или durationSeconds.
- Добавлены GET /api/workouts и GET /api/workouts/{id} с ограничением на доступ только к тренировкам текущего пользователя,
  поддержкой limit/offset, а также загрузкой упражнений и подходов для детального просмотра.
- Добавлена миграция V2__workout_finish.sql: finished_at в workout_session и индекс по (user_id, finished_at desc) для
  завершенных тренировок.
- Добавлен POST /api/workouts/{workoutId}/finish с идемпотентным завершением тренировки, валидацией пустых подходов
  (подход должен иметь duration или reps+weight) и блокировкой изменений после завершения (код ошибки WORKOUT_FINISHED).
- Проверка: ./mvnw test.
- B12 — workout response includes exerciseName/type в `WorkoutSessionResponse.WorkoutExerciseResponse`; добавлены OpenAPI-описания полей и уточнена релевантность полей подхода для TIME vs REPS_WEIGHT.

- Workout API: GET `/api/workouts` расширен параметром `status` (`ALL` по умолчанию, `FINISHED`, `IN_PROGRESS`) с фильтрацией по `finished_at`, сортировкой `startedAt desc`, и прежней пагинацией `limit/offset` только в рамках текущего пользователя.
- Workout API: POST `/api/workouts/start` теперь проверяет активную тренировку пользователя (`finished_at is null`) и возвращает `409 CONFLICT` с `ApiError.code=WORKOUT_ALREADY_IN_PROGRESS` и `ApiError.data.currentWorkoutId`.
- OpenAPI: для GET `/api/workouts` задокументирован query-параметр `status` с default=`ALL`, для POST `/api/workouts/start` добавлен ответ `409`.
- Примеры:
  - Запрос: `GET /api/workouts?status=FINISHED&limit=20&offset=0`
  - Ответ (200): `[ { "id": 101, "title": "2026-02-12 Push", "finishedAt": "2026-02-12T10:30:00Z" } ]`
  - Запрос: `POST /api/workouts/start`
  - Ответ (409): `{ "code": "WORKOUT_ALREADY_IN_PROGRESS", "data": { "currentWorkoutId": 55 } }`
