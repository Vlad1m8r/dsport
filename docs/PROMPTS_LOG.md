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
