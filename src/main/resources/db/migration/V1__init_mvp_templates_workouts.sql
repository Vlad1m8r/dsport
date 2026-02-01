-- V1__init_mvp_templates_workouts.sql
-- PostgreSQL 16

-- ================
-- Enums
-- ================
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'exercise_type') THEN
    CREATE TYPE exercise_type AS ENUM ('REPS_WEIGHT', 'TIME');
  END IF;
END $$;

-- ================
-- Users
-- ================
CREATE TABLE IF NOT EXISTS app_user (
  id            BIGSERIAL PRIMARY KEY,
  tg_user_id    BIGINT NOT NULL UNIQUE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ================
-- Muscle groups
-- ================
CREATE TABLE IF NOT EXISTS muscle_group (
  code          TEXT PRIMARY KEY,
  name          TEXT NOT NULL
);

INSERT INTO muscle_group (code, name) VALUES
  ('CHEST', 'Chest'),
  ('BACK', 'Back'),
  ('LEGS', 'Legs'),
  ('SHOULDERS', 'Shoulders'),
  ('ARMS', 'Arms'),
  ('CORE', 'Core')
ON CONFLICT (code) DO NOTHING;

-- ================
-- Exercises
-- ================
CREATE TABLE IF NOT EXISTS exercise (
  id            BIGSERIAL PRIMARY KEY,
  name          TEXT NOT NULL,
  type          exercise_type NOT NULL,
  owner_user_id BIGINT NULL REFERENCES app_user(id),
  deleted_at    TIMESTAMPTZ NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_exercise_owner_name
  ON exercise (owner_user_id, lower(name))
  WHERE deleted_at IS NULL;

-- Exercise ↔ MuscleGroup
CREATE TABLE IF NOT EXISTS exercise_muscle_group (
  exercise_id   BIGINT NOT NULL REFERENCES exercise(id) ON DELETE CASCADE,
  muscle_code   TEXT NOT NULL REFERENCES muscle_group(code),
  PRIMARY KEY (exercise_id, muscle_code)
);

-- ================
-- Workout Templates (Plan)
-- ================
CREATE TABLE IF NOT EXISTS workout_template (
  id            BIGSERIAL PRIMARY KEY,
  owner_user_id BIGINT NOT NULL REFERENCES app_user(id),
  name          TEXT NOT NULL,
  deleted_at    TIMESTAMPTZ NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS template_exercise (
  id            BIGSERIAL PRIMARY KEY,
  template_id   BIGINT NOT NULL REFERENCES workout_template(id) ON DELETE CASCADE,
  exercise_id   BIGINT NOT NULL REFERENCES exercise(id),
  order_index   INT NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_template_exercise_template
  ON template_exercise (template_id, order_index);

CREATE TABLE IF NOT EXISTS template_set (
  id                      BIGSERIAL PRIMARY KEY,
  template_exercise_id    BIGINT NOT NULL REFERENCES template_exercise(id) ON DELETE CASCADE,
  planned_reps            INT NULL,
  planned_duration_seconds INT NULL,
  order_index             INT NOT NULL,
  CONSTRAINT ck_template_set_oneof
    CHECK (
      (planned_reps IS NOT NULL AND planned_duration_seconds IS NULL)
      OR
      (planned_reps IS NULL AND planned_duration_seconds IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS ix_template_set_exercise
  ON template_set (template_exercise_id, order_index);

-- ================
-- Workout Sessions (Fact)
-- ================
CREATE TABLE IF NOT EXISTS workout_session (
  id            BIGSERIAL PRIMARY KEY,
  user_id       BIGINT NOT NULL REFERENCES app_user(id),
  template_id   BIGINT NULL REFERENCES workout_template(id),
  title         TEXT NOT NULL,
  started_at    TIMESTAMPTZ NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_workout_session_user_started
  ON workout_session (user_id, started_at DESC);

CREATE TABLE IF NOT EXISTS workout_exercise (
  id                 BIGSERIAL PRIMARY KEY,
  workout_session_id BIGINT NOT NULL REFERENCES workout_session(id) ON DELETE CASCADE,
  exercise_id        BIGINT NOT NULL REFERENCES exercise(id),
  order_index        INT NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_workout_exercise_session
  ON workout_exercise (workout_session_id, order_index);

CREATE INDEX IF NOT EXISTS ix_workout_exercise_exercise_session
  ON workout_exercise (exercise_id, workout_session_id);

CREATE TABLE IF NOT EXISTS set_entry (
  id                 BIGSERIAL PRIMARY KEY,
  workout_exercise_id BIGINT NOT NULL REFERENCES workout_exercise(id) ON DELETE CASCADE,
  reps               INT NULL,
  weight             NUMERIC(7,2) NULL,
  duration_seconds   INT NULL,
  order_index        INT NOT NULL,
  CONSTRAINT ck_set_entry_not_empty
    CHECK (reps IS NOT NULL OR duration_seconds IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS ix_set_entry_workout_exercise
  ON set_entry (workout_exercise_id, order_index);
