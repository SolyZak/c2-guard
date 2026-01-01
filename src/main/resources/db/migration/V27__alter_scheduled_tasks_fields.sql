ALTER TABLE scheduled_tasks ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE scheduled_task_execution_logs ALTER COLUMN status SET NOT NULL;
