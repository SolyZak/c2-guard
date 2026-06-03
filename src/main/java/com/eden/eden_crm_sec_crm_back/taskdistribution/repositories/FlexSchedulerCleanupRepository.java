package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Bulk-deletes flex-scheduler rows that point at the given execution-slot ids.
 *
 * <p>This is the one place in the codebase that reaches into
 * {@code scheduled_tasks} (the flex-scheduler 0.0.4 schema) instead of going
 * through {@code TaskSchedulerService.delete(uuid)}. We do this because the
 * library exposes no bulk delete and no metadata-based lookup, while task
 * removal during patrol/assignment edits can touch hundreds of slots at once.
 *
 * <p>If flex-scheduler ever changes the table or column name, the smoke test
 * {@code FlexSchedulerCleanupRepositoryTest} will fail on the next CI build.
 */
@Repository
@RequiredArgsConstructor
public class FlexSchedulerCleanupRepository {

    @PersistenceContext
    private final EntityManager em;

    /** Deletes every scheduled_tasks row whose {@code arguments->>'executionSlotId'} is in {@code slotIds}. */
    @Transactional
    public int deleteJobsForSlotIds(Collection<Long> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) return 0;
        // PostgreSQL JSONB ->> returns text; coerce slot ids to strings on the IN list.
        List<String> stringified = slotIds.stream().map(String::valueOf).toList();
        return em.createNativeQuery(
                "DELETE FROM scheduled_tasks " +
                "WHERE arguments->>'executionSlotId' IN (:slotIds)")
                .setParameter("slotIds", stringified)
                .executeUpdate();
    }
}
