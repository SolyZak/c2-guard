-- Update task_assignment table - make slot_number and workforce_id nullable with positive check
ALTER TABLE task_assignment 
ALTER COLUMN slot_number DROP NOT NULL,
ALTER COLUMN workforce_id DROP NOT NULL;

-- Add positive check constraint for slot_number
ALTER TABLE task_assignment 
ADD CONSTRAINT chk_task_assignment_slot_number_positive CHECK (slot_number > 0);

-- Update task_distribution table - make FKs NOT NULL
ALTER TABLE task_distribution 
ALTER COLUMN contract_id SET NOT NULL,
ALTER COLUMN customer_id SET NOT NULL,
ALTER COLUMN task_id SET NOT NULL;

-- Update task_execution_slot table - make FKs NOT NULL except task_execution_id
ALTER TABLE task_execution_slot 
ALTER COLUMN task_distribution_id SET NOT NULL,
ALTER COLUMN task_assignment_id SET NOT NULL,
ALTER COLUMN customer_id SET NOT NULL;

-- Update immediate_task_distribution table - make FKs NOT NULL except location_id
ALTER TABLE immediate_task_distribution 
ALTER COLUMN task_distribution_id SET NOT NULL,
ALTER COLUMN dispatcher_id SET NOT NULL,
ALTER COLUMN customer_id SET NOT NULL;

-- Update patrol_task_distribution table - make all FKs NOT NULL
ALTER TABLE patrol_task_distribution 
ALTER COLUMN task_distribution_id SET NOT NULL,
ALTER COLUMN patrol_detail_id SET NOT NULL,
ALTER COLUMN service_id SET NOT NULL,
ALTER COLUMN location_id SET NOT NULL,
ALTER COLUMN service_time_id SET NOT NULL,
ALTER COLUMN customer_id SET NOT NULL;
