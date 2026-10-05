-- Question bank entries are linked to lessons via lesson_question, not question.lesson_id.
-- Run once if inserts fail with: Column 'lesson_id' cannot be null

ALTER TABLE question
    MODIFY COLUMN lesson_id BIGINT NULL;
