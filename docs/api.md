# API Overview

## Основные группы эндпоинтов

### Templates
- POST /api/templates
- GET /api/templates
- GET /api/templates/{id}
- PUT /api/templates/{id}
- DELETE /api/templates/{id}

### Workouts
- POST /api/workouts/start
- DELETE /api/workouts/{workoutId}/exercises/{workoutExerciseId}
- POST /api/workouts/{workoutId}/exercises
- POST /api/workouts/{workoutId}/exercises/{workoutExerciseId}/sets
- PATCH /api/workouts/{workoutId}/sets/{setEntryId}
- DELETE /api/workouts/{workoutId}/exercises/{workoutExerciseId}/sets/{setEntryId}

### Stats
- GET /api/exercises/{exerciseId}/last-max

## Документация
- Все эндпоинты описаны через OpenAPI (springdoc).
- Swagger используется как live-документация.
