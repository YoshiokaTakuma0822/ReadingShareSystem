-- Add message_type and survey_id columns to chat_messages table
ALTER TABLE chat_messages
ADD COLUMN message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
ADD COLUMN survey_id UUID;

-- Add foreign key constraint for survey_id
ALTER TABLE chat_messages
ADD CONSTRAINT fk_chat_messages_survey_id
FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE SET NULL;

-- Create index for survey_id
CREATE INDEX idx_chat_messages_survey_id ON chat_messages(survey_id);

-- Create index for message_type
CREATE INDEX idx_chat_messages_message_type ON chat_messages(message_type);
