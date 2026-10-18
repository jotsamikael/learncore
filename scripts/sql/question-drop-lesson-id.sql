-- Questions are assigned to lessons via lesson_question, not question.lesson_id.
-- Run on learncore DB before/after removing Question.lesson from the JPA model.

USE learncore;

-- Remove references to missing lessons (blocks FK creation)
UPDATE question q
LEFT JOIN lesson l ON q.lesson_id = l.id
SET q.lesson_id = NULL
WHERE q.lesson_id IS NOT NULL
  AND l.id IS NULL;

-- Drop FK if it exists (name from your Hibernate log may differ)
SET @fk_name := (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'question'
      AND COLUMN_NAME = 'lesson_id'
      AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);

SET @drop_fk := IF(
    @fk_name IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE question DROP FOREIGN KEY `', @fk_name, '`')
);

PREPARE stmt FROM @drop_fk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE question DROP COLUMN lesson_id;
