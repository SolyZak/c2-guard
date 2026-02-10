ALTER TABLE public.contract_operation_site_distribution_activities
    DROP CONSTRAINT IF EXISTS contract_operation_site_distribution_activitie_activities_check;

ALTER TABLE public.contract_operation_site_distribution_activities
    ADD CONSTRAINT contract_operation_site_distribution_activitie_activities_check
    CHECK (
        (activities)::text = ANY (
            ARRAY[
                'ATTENDANCE'::text,
                'PATROLS'::text,
                'VISITORS'::text,
                'INCIDENTS'::text,
                'INSTANT_TASKS'::text
            ]
        )
    );

ALTER TABLE public.customer_service_activities
    DROP CONSTRAINT IF EXISTS customer_service_activities_activities_check;

ALTER TABLE public.customer_service_activities
    ADD CONSTRAINT customer_service_activities_activities_check
    CHECK (
        (activities)::text = ANY (
            ARRAY[
                'ATTENDANCE'::text,
                'PATROLS'::text,
                'VISITORS'::text,
                'INCIDENTS'::text,
                'INSTANT_TASKS'::text
            ]
        )
    );