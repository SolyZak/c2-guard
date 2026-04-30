ALTER TABLE task_check_comparison
    ADD COLUMN customer_id BIGINT;

UPDATE task_check_comparison
SET customer_id = (
    SELECT tce.customer_id
    FROM task_check_execution tce
    WHERE tce.id = task_check_comparison.task_check_execution_id
)
WHERE customer_id IS NULL;

ALTER TABLE task_check_comparison
    ALTER COLUMN customer_id SET NOT NULL;