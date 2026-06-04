-- In-place patrol edit needs to "remove" a task/location from the patrol
-- definition while keeping any past/in-progress execution history that still
-- references the patrol_detail row (FK from patrol_task_distribution). A soft
-- delete flag lets removed rows drop out of the definition/edit view while
-- their historical distributions stay intact.

ALTER TABLE patrol_detail
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Most reads of a patrol's definition filter on deleted = false; index the
-- active rows per patrol.
CREATE INDEX idx_patrol_detail_patrol_active
    ON patrol_detail (patrol_id)
    WHERE deleted = FALSE;
