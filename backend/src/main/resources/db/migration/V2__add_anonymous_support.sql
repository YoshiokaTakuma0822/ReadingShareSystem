-- Add is_anonymous column to survey_answers table
ALTER TABLE survey_answers ADD COLUMN is_anonymous BOOLEAN DEFAULT FALSE;

-- Create index for better performance on anonymous queries
CREATE INDEX idx_survey_answers_is_anonymous ON survey_answers(is_anonymous);
