# Changelog

## 2026-02-10
- Исправлена фильтрация `ExerciseRepository.findSummaryRowsForPicker` при `query`: убран вызов `lower()` на параметре JPQL, чтобы исключить ошибку PostgreSQL `ERROR: function lower(bytea) does not exist`.
- Нормализация `query` перенесена в сервис (`trim` + `toLowerCase(Locale.ROOT)`), в репозиторий передаётся уже нормализованная строка.

