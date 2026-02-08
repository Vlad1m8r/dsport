# Backend Changelog

- Исправлена проекция времени для last-max (Instant вместо OffsetDateTime) и возвращаются нулевые значения
  maxWeight/maxDurationSeconds при отсутствии данных.
- Добавлен PATCH /api/workouts/{workoutId}/sets/{setEntryId}: частичное обновление подхода с проверкой ownership
  (принадлежность подхода тренировке и пользователю) и валидацией, что после обновления задано reps или durationSeconds.
- Добавлены GET /api/workouts и GET /api/workouts/{id} с ограничением на доступ только к тренировкам текущего пользователя,
  поддержкой limit/offset, а также загрузкой упражнений и подходов для детального просмотра.
- Проверка: ./mvnw test.
