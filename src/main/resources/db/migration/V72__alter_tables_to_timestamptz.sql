ALTER TABLE public.task_check_comparison
    ALTER COLUMN created_date SET DATA TYPE timestamptz;

ALTER TABLE public.task_location_checks_image
    ALTER COLUMN created_date SET DATA TYPE timestamptz;