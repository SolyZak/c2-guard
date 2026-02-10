 --    - Seed permissions + classes + screens
--    - Map permissions to screens
--    - Fail fast if required rows are missing or screen names are ambiguous

------------------------------------------------------------
-- 1) Seed permissions (idempotent)
------------------------------------------------------------
INSERT INTO public."permission" (keycloak_role_name, name_en, name_ar)
VALUES
  ('statistics.view', 'View statistics', 'عرض الإحصائيات'),
  ('control unit.view', 'View control unit', 'عرض وحدة التحكم'),
  ('control unit.dispatch', 'Dispatch control unit', 'إرسال وحدة التحكم'),
  ('alert trigger.view', 'View alert triggers', 'عرض مشغلات التنبيهات'),
  ('alert trigger.Edit', 'Edit alert triggers', 'تعديل مشغلات التنبيهات'),

  ('premise.view', 'View premise', 'عرض الموقع'),
  ('premise.add', 'Add premise', 'إضافة موقع'),
  ('premise.edit', 'Edit premise', 'تعديل موقع'),
  ('premise.delete', 'Delete premise', 'حذف موقع'),

  ('operationsite.view', 'View operation site', 'عرض موقع التشغيل'),
  ('operationsite.add', 'Add operation site', 'إضافة موقع تشغيل'),
  ('operationsite.edit', 'Edit operation site', 'تعديل موقع تشغيل'),
  ('operationsite.delete', 'Delete operation site', 'حذف موقع تشغيل'),

  ('location.view', 'View location', 'عرض الموقع'),
  ('location.add', 'Add location', 'إضافة موقع'),
  ('location.edit', 'Edit location', 'تعديل موقع'),
  ('location.delete', 'Delete location', 'حذف موقع'),

  ('service.add', 'Add service', 'إضافة خدمة'),
  ('service.view', 'View services', 'عرض الخدمات'),

  ('contracts.view', 'View contracts', 'عرض العقود'),
  ('contracts.add', 'Add contracts', 'إضافة عقود'),
  ('contracts.send', 'Send contracts', 'إرسال العقود'),
  ('contract.view detail', 'View contract details', 'عرض تفاصيل العقد'),

  ('operation rule.view', 'View operation rules', 'عرض قواعد التشغيل'),
  ('operation rule.add', 'Add operation rules', 'إضافة قواعد التشغيل'),

  ('operate contract.view', 'View contract operation', 'عرض تشغيل العقد'),
  ('operate contract.distribute', 'Distribute contract', 'توزيع العقد'),

  ('tasks.view', 'View tasks', 'عرض المهام'),
  ('tasks.add', 'Add tasks', 'إضافة مهام'),

  ('patrol.view', 'View patrols', 'عرض الدوريات'),
  ('patrol.add', 'Add patrols', 'إضافة دوريات'),

  ('users.view', 'View users', 'عرض المستخدمين'),
  ('user.add', 'Add user', 'إضافة مستخدم'),
  ('user.reset', 'Reset user', 'إعادة تعيين المستخدم'),

  ('complaints.view', 'View complaints', 'عرض الشكاوى'),
  ('complaints.add', 'Add complaints', 'إضافة شكاوى'),

  ('permessions.view', 'View permissions', 'عرض الصلاحيات'),
  ('permessions.add', 'Add permissions', 'إضافة صلاحيات'),

  ('vehicle.view', 'View vehicles', 'عرض المركبات'),
  ('vehicle.add', 'Add vehicles', 'إضافة مركبات'),

  ('attendance report.view', 'View attendance report', 'عرض تقرير الحضور'),
  ('Contract statistics.view', 'View contract statistics', 'عرض إحصائيات العقود'),

  ('Visitor permission report.view', 'View visitor permission report', 'عرض تقرير تصاريح الزوار'),
  ('Visitor entrance report.view', 'View visitor entrance report', 'عرض تقرير دخول الزوار'),

  ('Vehicle permissions report.view', 'View vehicle permissions report', 'عرض تقرير تصاريح المركبات'),
  ('Vehicle entrance report.view', 'View vehicle entrance report', 'عرض تقرير دخول المركبات'),

  ('Incident report.view', 'View incident report', 'عرض تقرير الحوادث'),
  ('Patrol report.view', 'View patrol report', 'عرض تقرير الدوريات'),
  ('Complaints report.view', 'View complaints report', 'عرض تقرير الشكاوى')
ON CONFLICT (keycloak_role_name) DO NOTHING;


------------------------------------------------------------
-- 2) Seed permission_class (idempotent)
------------------------------------------------------------
INSERT INTO public.permission_class (name_en, name_ar)
VALUES
  ('Command & Control', 'القيادة والتحكم'),
  ('Premises and Sites', 'المواقع والمنشآت'),
  ('Contract Management', 'إدارة العقود'),
  ('Contract Operation', 'تشغيل العقود'),
  ('Users Management', 'إدارة المستخدمين'),
  ('Complaints', 'الشكاوى'),
  ('Permissions', 'الصلاحيات'),
  ('Reports', 'التقارير')
ON CONFLICT (name_en) DO NOTHING;


------------------------------------------------------------
-- 3) Validate required permission_class exist (fail fast)
------------------------------------------------------------
DO
$$
DECLARE missing text;
BEGIN
  SELECT string_agg(v.name_en, ', ' ORDER BY v.name_en)
  INTO missing
  FROM (VALUES
    ('Command & Control'),
    ('Premises and Sites'),
    ('Contract Management'),
    ('Contract Operation'),
    ('Users Management'),
    ('Complaints'),
    ('Permissions'),
    ('Reports')
  ) AS v(name_en)
  WHERE NOT EXISTS (
    SELECT 1 FROM public.permission_class pc WHERE pc.name_en = v.name_en
  );

  IF missing IS NOT NULL THEN
    RAISE EXCEPTION 'Missing permission_class rows (name_en): %', missing;
  END IF;
END
$$
;


------------------------------------------------------------
-- 4) Seed permission_screen (idempotent)
------------------------------------------------------------
WITH screen_seed AS (
  SELECT * FROM (VALUES
    ('Command & Control', 'Statistics', 'الإحصائيات'),
    ('Command & Control', 'Control Unit', 'وحدة التحكم'),
    ('Command & Control', 'Severity Config', 'إعدادات مستوى الخطورة'),

    ('Premises and Sites', 'Premises', 'المنشآت'),
    ('Premises and Sites', 'Operation Site', 'موقع التشغيل'),
    ('Premises and Sites', 'Location', 'المواقع'),

    ('Contract Management', 'Services', 'الخدمات'),
    ('Contract Management', 'Contracts', 'العقود'),
    ('Contract Management', 'Operation Rules', 'قواعد التشغيل'),

    ('Contract Operation', 'Operate Contract', 'تشغيل العقد'),
    ('Contract Operation', 'Tasks', 'المهام'),
    ('Contract Operation', 'Patrol', 'الدوريات'),

    ('Users Management', 'Users', 'المستخدمون'),

    ('Complaints', 'Complaints', 'الشكاوى'),

    ('Permissions', 'Issuing Permissions', 'إصدار التصاريح'),
    ('Permissions', 'Vehicle Permissions', 'تصاريح المركبات'),

    ('Reports', 'Attendance Report', 'تقرير الحضور'),
    ('Reports', 'Contract Statistics', 'إحصائيات العقود'),
    ('Reports', 'Visitor Permission Report', 'تقرير تصاريح الزوار'),
    ('Reports', 'Visitor Entrance Report', 'تقرير دخول الزوار'),
    ('Reports', 'Vehicle Permissions Report', 'تقرير تصاريح المركبات'),
    ('Reports', 'Vehicle Entrance Report', 'تقرير دخول المركبات'),
    ('Reports', 'Incident Report', 'تقرير الحوادث'),
    ('Reports', 'Patrol Report', 'تقرير الدوريات'),
    ('Reports', 'Complaints Report', 'تقرير الشكاوى')
  ) AS t(class_name_en, screen_name_en, screen_name_ar)
)
INSERT INTO public.permission_screen (permission_class_id, name_en, name_ar)
SELECT pc.id, ss.screen_name_en, ss.screen_name_ar
FROM screen_seed ss
JOIN public.permission_class pc ON pc.name_en = ss.class_name_en
ON CONFLICT (permission_class_id, name_en) DO NOTHING;


------------------------------------------------------------
-- 5) Validate required screens exist + are not ambiguous (fail fast)
------------------------------------------------------------
DO
$$
DECLARE missing text;
DECLARE ambiguous text;
BEGIN
  -- Missing screens by (class_name_en, screen_name_en)
  SELECT string_agg(format('%s -> %s', v.class_name_en, v.screen_name_en), ' | ' ORDER BY v.class_name_en, v.screen_name_en)
  INTO missing
  FROM (VALUES
    ('Command & Control', 'Statistics'),
    ('Command & Control', 'Control Unit'),
    ('Command & Control', 'Severity Config'),

    ('Premises and Sites', 'Premises'),
    ('Premises and Sites', 'Operation Site'),
    ('Premises and Sites', 'Location'),

    ('Contract Management', 'Services'),
    ('Contract Management', 'Contracts'),
    ('Contract Management', 'Operation Rules'),

    ('Contract Operation', 'Operate Contract'),
    ('Contract Operation', 'Tasks'),
    ('Contract Operation', 'Patrol'),

    ('Users Management', 'Users'),

    ('Complaints', 'Complaints'),

    ('Permissions', 'Issuing Permissions'),
    ('Permissions', 'Vehicle Permissions'),

    ('Reports', 'Attendance Report'),
    ('Reports', 'Contract Statistics'),
    ('Reports', 'Visitor Permission Report'),
    ('Reports', 'Visitor Entrance Report'),
    ('Reports', 'Vehicle Permissions Report'),
    ('Reports', 'Vehicle Entrance Report'),
    ('Reports', 'Incident Report'),
    ('Reports', 'Patrol Report'),
    ('Reports', 'Complaints Report')
  ) AS v(class_name_en, screen_name_en)
  WHERE NOT EXISTS (
    SELECT 1
    FROM public.permission_screen ps
    JOIN public.permission_class pc ON pc.id = ps.permission_class_id
    WHERE pc.name_en = v.class_name_en
      AND ps.name_en = v.screen_name_en
  );

  IF missing IS NOT NULL THEN
    RAISE EXCEPTION 'Missing permission_screen rows: %', missing;
  END IF;

  -- Ensure each required screen name is UNIQUE globally (so mapping by name_en is safe)
  SELECT string_agg(t.screen_name_en, ', ' ORDER BY t.screen_name_en)
  INTO ambiguous
  FROM (
    SELECT v.screen_name_en
    FROM (VALUES
      ('Statistics'),
      ('Control Unit'),
      ('Severity Config'),
      ('Premises'),
      ('Operation Site'),
      ('Location'),
      ('Services'),
      ('Contracts'),
      ('Operation Rules'),
      ('Operate Contract'),
      ('Tasks'),
      ('Patrol'),
      ('Users'),
      ('Complaints'),
      ('Issuing Permissions'),
      ('Vehicle Permissions'),
      ('Attendance Report'),
      ('Contract Statistics'),
      ('Visitor Permission Report'),
      ('Visitor Entrance Report'),
      ('Vehicle Permissions Report'),
      ('Vehicle Entrance Report'),
      ('Incident Report'),
      ('Patrol Report'),
      ('Complaints Report')
    ) AS v(screen_name_en)
    JOIN public.permission_screen ps ON ps.name_en = v.screen_name_en
    GROUP BY v.screen_name_en
    HAVING COUNT(*) <> 1
  ) t;

  IF ambiguous IS NOT NULL THEN
    RAISE EXCEPTION 'permission_screen.name_en must be unique for mapping; problematic names: %', ambiguous;
  END IF;
END
$$
;


------------------------------------------------------------
-- 6) Validate required permissions exist (fail fast)
------------------------------------------------------------
DO
$$
DECLARE missing text;
BEGIN
  SELECT string_agg(v.permission_key, ', ' ORDER BY v.permission_key)
  INTO missing
  FROM (VALUES
    ('statistics.view'),
    ('control unit.view'),
    ('control unit.dispatch'),
    ('alert trigger.view'),
    ('alert trigger.Edit'),

    ('premise.view'),
    ('premise.add'),
    ('premise.edit'),
    ('premise.delete'),

    ('operationsite.view'),
    ('operationsite.add'),
    ('operationsite.edit'),
    ('operationsite.delete'),

    ('location.view'),
    ('location.add'),
    ('location.edit'),
    ('location.delete'),

    ('service.add'),
    ('service.view'),

    ('contracts.view'),
    ('contracts.add'),
    ('contracts.send'),
    ('contract.view detail'),

    ('operation rule.view'),
    ('operation rule.add'),

    ('operate contract.view'),
    ('operate contract.distribute'),

    ('tasks.view'),
    ('tasks.add'),

    ('patrol.view'),
    ('patrol.add'),

    ('users.view'),
    ('user.add'),
    ('user.reset'),

    ('complaints.view'),
    ('complaints.add'),

    ('permessions.view'),
    ('permessions.add'),

    ('vehicle.view'),
    ('vehicle.add'),

    ('attendance report.view'),
    ('Contract statistics.view'),

    ('Visitor permission report.view'),
    ('Visitor entrance report.view'),

    ('Vehicle permissions report.view'),
    ('Vehicle entrance report.view'),

    ('Incident report.view'),
    ('Patrol report.view'),
    ('Complaints report.view')
  ) AS v(permission_key)
  WHERE NOT EXISTS (
    SELECT 1 FROM public."permission" p WHERE p.keycloak_role_name = v.permission_key
  );

  IF missing IS NOT NULL THEN
    RAISE EXCEPTION 'Missing permission rows (keycloak_role_name): %', missing;
  END IF;
END
$$
;


------------------------------------------------------------
-- 7) Link permissions to screens (by key)
------------------------------------------------------------
WITH perm_to_screen AS (
  SELECT * FROM (VALUES
    -- Command & Control
    ('statistics.view', 'Statistics'),
    ('control unit.view', 'Control Unit'),
    ('control unit.dispatch', 'Control Unit'),

    --"Severity Config"
    ('alert trigger.view', 'Severity Config'),
    ('alert trigger.Edit', 'Severity Config'),

    -- Premises and Sites
    ('premise.view', 'Premises'),
    ('premise.add', 'Premises'),
    ('premise.edit', 'Premises'),
    ('premise.delete', 'Premises'),

    ('operationsite.view', 'Operation Site'),
    ('operationsite.add', 'Operation Site'),
    ('operationsite.edit', 'Operation Site'),
    ('operationsite.delete', 'Operation Site'),

    ('location.view', 'Location'),
    ('location.add', 'Location'),
    ('location.edit', 'Location'),
    ('location.delete', 'Location'),

    -- Contract Management
    ('service.add', 'Services'),
    ('service.view', 'Services'),

    ('contracts.view', 'Contracts'),
    ('contracts.add', 'Contracts'),
    ('contracts.send', 'Contracts'),
    ('contract.view detail', 'Contracts'),

    ('operation rule.view', 'Operation Rules'),
    ('operation rule.add', 'Operation Rules'),

    -- Contract Operation
    ('operate contract.view', 'Operate Contract'),
    ('operate contract.distribute', 'Operate Contract'),

    ('tasks.view', 'Tasks'),
    ('tasks.add', 'Tasks'),

    ('patrol.view', 'Patrol'),
    ('patrol.add', 'Patrol'),

    -- Users Management
    ('users.view', 'Users'),
    ('user.add', 'Users'),
    ('user.reset', 'Users'),

    -- Complaints
    ('complaints.view', 'Complaints'),
    ('complaints.add', 'Complaints'),

    -- Permissions
    ('permessions.view', 'Issuing Permissions'),
    ('permessions.add', 'Issuing Permissions'),

    ('vehicle.view', 'Vehicle Permissions'),
    ('vehicle.add', 'Vehicle Permissions'),

    -- Reports
    ('attendance report.view', 'Attendance Report'),
    ('Contract statistics.view', 'Contract Statistics'),

    ('Visitor permission report.view', 'Visitor Permission Report'),
    ('Visitor entrance report.view', 'Visitor Entrance Report'),

    ('Vehicle permissions report.view', 'Vehicle Permissions Report'),
    ('Vehicle entrance report.view', 'Vehicle Entrance Report'),

    ('Incident report.view', 'Incident Report'),
    ('Patrol report.view', 'Patrol Report'),
    ('Complaints report.view', 'Complaints Report')
  ) AS t(permission_key, screen_name_en)
)
UPDATE public."permission" p
SET permission_screen_id = ps.id
FROM perm_to_screen m
JOIN public.permission_screen ps ON ps.name_en = m.screen_name_en
WHERE p.keycloak_role_name = m.permission_key;


------------------------------------------------------------
-- 8) Validate mapping applied correctly (fail fast)
------------------------------------------------------------
DO
$$
DECLARE bad text;
BEGIN
  SELECT string_agg(format('%s -> %s', m.permission_key, m.screen_name_en), ' | ' ORDER BY m.permission_key)
  INTO bad
  FROM (VALUES
    ('statistics.view', 'Statistics'),
    ('control unit.view', 'Control Unit'),
    ('control unit.dispatch', 'Control Unit'),
    ('alert trigger.view', 'Severity Config'),
    ('alert trigger.Edit', 'Severity Config'),

    ('premise.view', 'Premises'),
    ('premise.add', 'Premises'),
    ('premise.edit', 'Premises'),
    ('premise.delete', 'Premises'),

    ('operationsite.view', 'Operation Site'),
    ('operationsite.add', 'Operation Site'),
    ('operationsite.edit', 'Operation Site'),
    ('operationsite.delete', 'Operation Site'),

    ('location.view', 'Location'),
    ('location.add', 'Location'),
    ('location.edit', 'Location'),
    ('location.delete', 'Location'),

    ('service.add', 'Services'),
    ('service.view', 'Services'),

    ('contracts.view', 'Contracts'),
    ('contracts.add', 'Contracts'),
    ('contracts.send', 'Contracts'),
    ('contract.view detail', 'Contracts'),

    ('operation rule.view', 'Operation Rules'),
    ('operation rule.add', 'Operation Rules'),

    ('operate contract.view', 'Operate Contract'),
    ('operate contract.distribute', 'Operate Contract'),

    ('tasks.view', 'Tasks'),
    ('tasks.add', 'Tasks'),

    ('patrol.view', 'Patrol'),
    ('patrol.add', 'Patrol'),

    ('users.view', 'Users'),
    ('user.add', 'Users'),
    ('user.reset', 'Users'),

    ('complaints.view', 'Complaints'),
    ('complaints.add', 'Complaints'),

    ('permessions.view', 'Issuing Permissions'),
    ('permessions.add', 'Issuing Permissions'),

    ('vehicle.view', 'Vehicle Permissions'),
    ('vehicle.add', 'Vehicle Permissions'),

    ('attendance report.view', 'Attendance Report'),
    ('Contract statistics.view', 'Contract Statistics'),

    ('Visitor permission report.view', 'Visitor Permission Report'),
    ('Visitor entrance report.view', 'Visitor Entrance Report'),

    ('Vehicle permissions report.view', 'Vehicle Permissions Report'),
    ('Vehicle entrance report.view', 'Vehicle Entrance Report'),

    ('Incident report.view', 'Incident Report'),
    ('Patrol report.view', 'Patrol Report'),
    ('Complaints report.view', 'Complaints Report')
  ) AS m(permission_key, screen_name_en)
  JOIN public."permission" p ON p.keycloak_role_name = m.permission_key
  JOIN public.permission_screen ps ON ps.name_en = m.screen_name_en
  WHERE p.permission_screen_id IS DISTINCT FROM ps.id;

  IF bad IS NOT NULL THEN
    RAISE EXCEPTION 'Permission -> screen mapping failed for: %', bad;
  END IF;
END
$$
;