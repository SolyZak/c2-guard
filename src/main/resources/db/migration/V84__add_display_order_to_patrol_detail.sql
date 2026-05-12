ALTER TABLE patrol_detail ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

WITH ranked AS (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY patrol_id ORDER BY id) AS rn
    FROM patrol_detail
)
UPDATE patrol_detail pd
SET display_order = r.rn
FROM ranked r
WHERE pd.id = r.id;

CREATE INDEX idx_patrol_detail_patrol_order ON patrol_detail(patrol_id, display_order);
