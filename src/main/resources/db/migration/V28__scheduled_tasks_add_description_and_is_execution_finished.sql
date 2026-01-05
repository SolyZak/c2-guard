ALTER TABLE scheduled_tasks ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE scheduled_tasks ADD COLUMN description VARCHAR(500);
ALTER TABLE scheduled_tasks ADD COLUMN is_execution_finished BOOLEAN DEFAULT FALSE NOT NULL;


CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_is_execution_finished ON scheduled_tasks (is_execution_finished);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_typeOfExecution_is_execution_finished ON scheduled_tasks (type_of_execution, is_execution_finished);
