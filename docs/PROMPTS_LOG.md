# Prompts Log

- API: exercise picker — добавлены GET /api/muscle-groups и GET /api/exercises.
  По умолчанию возвращается полный список доступных пользователю упражнений (system + my),
  фильтры query/muscleGroup/scope остаются опциональными и применяются сервером.
- API: для /api/exercises добавлен summary DTO (id, name, type, muscleGroups, scope) и фиксированная сортировка
  (системные, затем пользовательские, внутри — по названию).
- OpenAPI: новые endpoint'ы и параметры с дефолтами описаны аннотациями, YAML будет сгенерирован отдельно.
- Docs: added DECISIONS_SET_TYPE.md with plan for reps/time defaults and future API changes.
- API: добавлено завершение тренировки (POST /api/workouts/{workoutId}/finish), finishedAt, проверка пустых подходов
  (duration или reps+weight) и запрет изменений после finish, плюс миграция V2__workout_finish.sql.
- API/Repository safety: в запросе exercise picker enum-параметр `scope` в JPQL заменён на `String`
  (`ALL`/`SYSTEM`/`MY`) и сравнения со строковыми литералами для устойчивости Hibernate 6.x
  (предотвращение `Could not determine ValueMapping for SqmParameter(...)`).
- Workout business rule: одновременно только одна активная тренировка на пользователя.
  - GET `/api/workouts` получил фильтр `status=ALL|FINISHED|IN_PROGRESS` (default `ALL`) + прежние `limit/offset`.
  - POST `/api/workouts/start` при наличии активной тренировки возвращает `409 WORKOUT_ALREADY_IN_PROGRESS`
    и `ApiError.data.currentWorkoutId`.
  - OpenAPI аннотации обновлены: `status` для списка тренировок и `409` для старта.
  - Примеры:
    - `GET /api/workouts?status=IN_PROGRESS` -> `[]` или `[ { ... "finishedAt": null ... } ]`
    - `POST /api/workouts/start` -> `409 { "code":"WORKOUT_ALREADY_IN_PROGRESS", "data": { "currentWorkoutId": 55 } }`
