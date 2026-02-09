# Prompts Log

- Docs: added DECISIONS_SET_TYPE.md with plan for reps/time defaults and future API changes.
- API: добавлено завершение тренировки (POST /api/workouts/{workoutId}/finish), finishedAt, проверка пустых подходов
  (duration или reps+weight) и запрет изменений после finish, плюс миграция V2__workout_finish.sql.
