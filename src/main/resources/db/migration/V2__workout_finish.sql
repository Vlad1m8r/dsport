-- V2__workout_finish.sql
ALTER TABLE workout_session
    ADD COLUMN finished_at TIMESTAMPTZ NULL;

CREATE INDEX IF NOT EXISTS ix_workout_session_user_finished
    ON workout_session (user_id, finished_at DESC)
    WHERE finished_at IS NOT NULL;
