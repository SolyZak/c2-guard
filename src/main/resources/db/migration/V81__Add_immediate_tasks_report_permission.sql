/*  V81__Add_immediate_tasks_report_permission.sql

    - Adds a new permission_screen "Immediate tasks report" under the existing
      "Reports" permission_class.
    - Adds a new permission "Immediate_tasks_report.view" and maps it to that screen.
    - Idempotent + fail-fast (matches the pattern used in V49).
    - Keycloak role is assumed to be already created manually.
*/

------------------------------------------------------------
-- 0) Prerequisites (safety: ensure unique indexes exist)
------------------------------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS ux_permission_keycloak_role_name
ON public."permission"(keycloak_role_name);

CREATE UNIQUE INDEX IF NOT EXISTS ux_permission_screen_classid_name_en
ON public.permission_screen(permission_class_id, name_en);

CREATE UNIQUE INDEX IF NOT EXISTS ux_permission_screen_name_en
ON public.permission_screen(name_en);


------------------------------------------------------------
-- 1) Validate that the "Reports" permission_class exists (fail fast)
------------------------------------------------------------
DO

$$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM public.permission_class WHERE name_en = 'Reports'
  ) THEN
    RAISE EXCEPTION 'Required permission_class "Reports" is missing. Run V49 first.';
  END IF;
END
$$
;


------------------------------------------------------------
-- 2) Seed the new permission_screen "Immediate tasks report" (idempotent)
------------------------------------------------------------
INSERT INTO public.permission_screen (permission_class_id, name_en, name_ar)
SELECT pc.id, 'Immediate tasks report', 'تقرير المهام الفورية'
FROM public.permission_class pc
WHERE pc.name_en = 'Reports'
ON CONFLICT (permission_class_id, name_en) DO NOTHING;


------------------------------------------------------------
-- 3) Validate that the screen exists and is unique (fail fast)
------------------------------------------------------------
DO

$$
DECLARE screen_count int;
BEGIN
  SELECT COUNT(*) INTO screen_count
  FROM public.permission_screen ps
  JOIN public.permission_class pc ON pc.id = ps.permission_class_id
  WHERE pc.name_en = 'Reports'
    AND ps.name_en = 'Immediate tasks report';

  IF screen_count = 0 THEN
    RAISE EXCEPTION 'Failed to create permission_screen "Immediate tasks report" under class "Reports".';
  ELSIF screen_count > 1 THEN
    RAISE EXCEPTION 'permission_screen "Immediate tasks report" under class "Reports" is ambiguous (count=%).', screen_count;
  END IF;
END
$$
;


------------------------------------------------------------
-- 4) Seed the new permission "Immediate_tasks_report.view" (idempotent)
------------------------------------------------------------
INSERT INTO public."permission" (keycloak_role_name, name_en, name_ar)
VALUES
  ('Immediate_tasks_report.view', 'View immediate tasks report', 'عرض تقرير المهام الفورية')
ON CONFLICT (keycloak_role_name) DO NOTHING;


------------------------------------------------------------
-- 5) Validate the permission exists (fail fast)
------------------------------------------------------------
DO

$$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM public."permission" WHERE keycloak_role_name = 'Immediate_tasks_report.view'
  ) THEN
    RAISE EXCEPTION 'Failed to create permission "Immediate_tasks_report.view".';
  END IF;
END
$$
;


------------------------------------------------------------
-- 6) Link the permission to its screen
------------------------------------------------------------
UPDATE public."permission" p
SET permission_screen_id = ps.id
FROM public.permission_screen ps
JOIN public.permission_class pc ON pc.id = ps.permission_class_id
WHERE pc.name_en = 'Reports'
  AND ps.name_en = 'Immediate tasks report'
  AND p.keycloak_role_name = 'Immediate_tasks_report.view';


------------------------------------------------------------
-- 7) Validate mapping applied correctly (fail fast)
------------------------------------------------------------
DO

$$
DECLARE mapped_screen_id int;
DECLARE expected_screen_id int;
BEGIN
  SELECT p.permission_screen_id INTO mapped_screen_id
  FROM public."permission" p
  WHERE p.keycloak_role_name = 'Immediate_tasks_report.view';

  SELECT ps.id INTO expected_screen_id
  FROM public.permission_screen ps
  JOIN public.permission_class pc ON pc.id = ps.permission_class_id
  WHERE pc.name_en = 'Reports'
    AND ps.name_en = 'Immediate tasks report';

  IF mapped_screen_id IS DISTINCT FROM expected_screen_id THEN
    RAISE EXCEPTION 'Permission -> screen mapping failed: permission_screen_id=% expected=%',
                    mapped_screen_id, expected_screen_id;
  END IF;
END
$$
;