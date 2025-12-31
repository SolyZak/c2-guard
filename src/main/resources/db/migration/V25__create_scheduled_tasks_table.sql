CREATE TABLE IF NOT EXISTS scheduled_tasks (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    task_type VARCHAR(255) NOT NULL,
    type_of_execution VARCHAR(50) NOT NULL,
    cron_expression VARCHAR(100),
    planned_execution_time TIMESTAMP WITH TIME ZONE,
    start_date_time TIMESTAMP WITH TIME ZONE,
    duration BIGINT,
    arguments JSONB,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_id ON scheduled_tasks (id);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_name ON scheduled_tasks (name);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_task_type ON scheduled_tasks (task_type);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_is_active ON scheduled_tasks (is_active);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_type_active ON scheduled_tasks (task_type, is_active);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_type_of_execution ON scheduled_tasks (type_of_execution);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_active_planned_execution_time ON scheduled_tasks (is_active, planned_execution_time);
CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_created_at ON scheduled_tasks (created_at);
