# Backend Changelog

- Исправлена проекция времени для last-max (Instant вместо OffsetDateTime) и возвращаются нулевые значения
  maxWeight/maxDurationSeconds при отсутствии данных.
- Добавлен PATCH /api/workouts/{workoutId}/sets/{setEntryId}: частичное обновление подхода с проверкой ownership
  (принадлежность подхода тренировке и пользователю) и валидацией, что после обновления задано reps или durationSeconds.
