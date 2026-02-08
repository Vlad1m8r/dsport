# Backend Changelog

- Добавлен PATCH /api/workouts/{workoutId}/sets/{setEntryId}: частичное обновление подхода с проверкой ownership
  (принадлежность подхода тренировке и пользователю) и валидацией, что после обновления задано reps или durationSeconds.
