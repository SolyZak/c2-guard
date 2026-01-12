CREATE TABLE IF NOT EXISTS scheduled_task_execution_logs (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) DEFAULT 'STARTED',
    result JSONB,
    
    CONSTRAINT fk_task
        FOREIGN KEY (task_id) 
        REFERENCES scheduled_tasks (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_scheduled_task_execution_logs_id ON scheduled_task_execution_logs (id);
CREATE INDEX IF NOT EXISTS idx_scheduled_task_execution_logs_task_id ON scheduled_task_execution_logs (task_id);
CREATE INDEX IF NOT EXISTS idx_scheduled_task_execution_logs_started_at ON scheduled_task_execution_logs (started_at);
CREATE INDEX IF NOT EXISTS idx_scheduled_task_execution_logs_finished_at ON scheduled_task_execution_logs (finished_at);
CREATE INDEX IF NOT EXISTS idx_scheduled_task_execution_logs_status ON scheduled_task_execution_logs (status);
