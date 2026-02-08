# База данных

## СУБД
PostgreSQL 16 (image: postgres:16-alpine)

## Миграции
- Все изменения схемы — только через Flyway.
- V1 — инициализация всей MVP схемы.
- Миграции immutable (не редактируются после применения).

## Soft delete
Используется для:
- exercise
- workout_template

Причина:
- не ломать историю тренировок,
- сохранять referential integrity.

## Важные ограничения
- В template_set:
  plannedReps XOR plannedDurationSeconds
- В set_entry:
  reps OR durationSeconds должен быть задан

## Индексы
Оптимизировано под:
- старт тренировки,
- поиск «последнего максимума»,
- историю пользователя.
