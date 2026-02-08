# Decisions: Set type (REPS/WEIGHT vs TIME) & defaults

## Контекст
- В БД есть constraint: ck_set_entry_not_empty => (reps IS NOT NULL) OR (duration_seconds IS NOT NULL).
- Сейчас при нажатии "Добавить подход" фронт может отправлять reps=null и durationSeconds=null, что ломает insert.
- В текущем API workout response часто содержит только exerciseId без exerciseType и без exerciseName.
- В результате фронт не знает, какой дефолт ставить: reps или durationSeconds.

## Текущее временное решение (MVP hotfix)
- При добавлении подхода фронт всегда отправляет reps=0 (durationSeconds=null, weight=null).
- Это удовлетворяет constraint, подход появляется в UI, а значения можно затем изменить.
- Ограничение: для тайм-упражнений это не идеально, но работает.

## Цель (правильное решение)
Фронт должен понимать тип упражнения/подхода и при добавлении подхода выбирать дефолты:
- Для силовых/повторных упражнений: reps default (например 0 или 1), durationSeconds = null
- Для тайм-упражнений: durationSeconds default (например 30), reps = null

## Варианты реализации (API contract)

### Вариант A (рекомендуемый): включить exerciseType в ответ тренировки
Изменение backend:
- В WorkoutSessionResponse / WorkoutExerciseResponse добавить:
  - exerciseType: "REPS_WEIGHT" | "TIME"
  - (опционально) exerciseName: string
- Источник: таблица exercise (уже содержит type)

Изменение frontend:
- При "Добавить подход" выбирать дефолт в зависимости от exerciseType.
- В UI отображать правильные поля (если TIME — показывать duration, если REPS — reps+weight).

Плюсы:
- 1 запрос на экран тренировки (без доп. справочника).
- Простая логика на фронте.
Минусы:
- Нужно расширить DTO и OpenAPI.

### Вариант B: отдельный справочник упражнений
Backend:
- Добавить endpoint GET /api/exercises (или GET /api/exercises/{id}) отдающий name + type (+muscle groups позже)
Frontend:
- На WorkoutPage при наличии списка exerciseIds подгружать details (batched или caching).
- Дефолты выбирать по type.

Плюсы:
- Полезно для template editor и exercise picker.
Минусы:
- Больше запросов, кэширование, синхронизация.

### Вариант C: хранить "setType" на уровне WorkoutExercise
Backend:
- При создании workout_exercise сохранять setType (копия из exercise.type).
Frontend:
- Использовать это поле.

Плюсы:
- Стабильно даже если exercise.type когда-то изменится.
Минусы:
- Дублирование данных.

## Рекомендуемый план внедрения (по шагам)
Step 1 (быстро, закрывает UX):
- Реализовать Вариант A: добавить exerciseType (+exerciseName) в workout response
- Обновить openapi.yaml
- Обновить фронт: показывать нормальные названия и выбирать дефолт для addSet

Step 2:
- Добавить exercise picker в WorkoutPage (поиск + список), для этого нужен endpoint списка упражнений (Вариант B, возможно позже)

Step 3:
- Ввести более строгую валидацию: если TIME — reps должен быть null, если REPS — durationSeconds null (опционально, позже)

## TODO
- [ ] Backend: добавить exerciseType (+exerciseName) в WorkoutSessionResponse
- [ ] Frontend: использовать exerciseType для выбора дефолта addSet и для UI полей
- [ ] Backend: endpoint exercises list/details (для picker и template editor)
- [ ] Обновить docs/openapi.yaml после расширения DTO
