ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS language_code TEXT NOT NULL DEFAULT 'en';

ALTER TABLE exercise
    ADD COLUMN IF NOT EXISTS preview_gif_url TEXT NULL,
    ADD COLUMN IF NOT EXISTS preview_image_url TEXT NULL,
    ADD COLUMN IF NOT EXISTS video_url TEXT NULL;

CREATE TABLE IF NOT EXISTS exercise_translation (
    id BIGSERIAL PRIMARY KEY,
    exercise_id BIGINT NOT NULL REFERENCES exercise(id) ON DELETE CASCADE,
    language_code TEXT NOT NULL,
    name TEXT NOT NULL,
    short_description TEXT NULL,
    description TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_exercise_translation_exercise_language UNIQUE (exercise_id, language_code)
);

CREATE INDEX IF NOT EXISTS ix_exercise_translation_exercise_id
    ON exercise_translation (exercise_id);

CREATE INDEX IF NOT EXISTS ix_exercise_translation_language_code
    ON exercise_translation (language_code);

INSERT INTO exercise_translation (
    exercise_id,
    language_code,
    name,
    short_description,
    description,
    created_at,
    updated_at
)
SELECT e.id,
       'en',
       e.name,
       NULL,
       NULL,
       e.created_at,
       e.updated_at
FROM exercise e
LEFT JOIN exercise_translation et
       ON et.exercise_id = e.id
      AND et.language_code = 'en'
WHERE e.name IS NOT NULL
  AND et.id IS NULL;

DROP INDEX IF EXISTS ux_exercise_owner_name;

ALTER TABLE exercise
    DROP COLUMN IF EXISTS name;

CREATE TABLE IF NOT EXISTS muscle_group_translation (
    id BIGSERIAL PRIMARY KEY,
    muscle_code TEXT NOT NULL REFERENCES muscle_group(code) ON DELETE CASCADE,
    language_code TEXT NOT NULL,
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_muscle_group_translation_code_language UNIQUE (muscle_code, language_code)
);

CREATE INDEX IF NOT EXISTS ix_muscle_group_translation_code
    ON muscle_group_translation (muscle_code);

CREATE INDEX IF NOT EXISTS ix_muscle_group_translation_language_code
    ON muscle_group_translation (language_code);

INSERT INTO muscle_group_translation (
    muscle_code,
    language_code,
    name,
    created_at,
    updated_at
)
SELECT mg.code,
       'en',
       mg.name,
       now(),
       now()
FROM muscle_group mg
LEFT JOIN muscle_group_translation mgt
       ON mgt.muscle_code = mg.code
      AND mgt.language_code = 'en'
WHERE mg.name IS NOT NULL
  AND mgt.id IS NULL;

ALTER TABLE muscle_group
    DROP COLUMN IF EXISTS name;
