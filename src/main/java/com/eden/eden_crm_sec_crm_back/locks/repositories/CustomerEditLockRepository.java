package com.eden.eden_crm_sec_crm_back.locks.repositories;

import com.eden.eden_crm_sec_crm_back.locks.entities.CustomerEditLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface CustomerEditLockRepository extends JpaRepository<CustomerEditLock, Long> {

    /**
     * Atomic acquire/refresh. Inserts a fresh row, or overwrites the existing row
     * only when (a) it has expired, or (b) the caller already holds it (refresh).
     * Returns 1 on success, 0 if held by another active user.
     */
    @Modifying
    @Query(value = """
            INSERT INTO patrol_edit_lock
                (patrol_id, holder_user_id, holder_user_name, acquired_at, expires_at)
            VALUES (:patrolId, :holderUserId, :holderName, :now, :expiresAt)
            ON CONFLICT (patrol_id) DO UPDATE
               SET holder_user_id   = EXCLUDED.holder_user_id,
                   holder_user_name = EXCLUDED.holder_user_name,
                   acquired_at      = EXCLUDED.acquired_at,
                   expires_at       = EXCLUDED.expires_at
             WHERE patrol_edit_lock.expires_at < :now
                OR patrol_edit_lock.holder_user_id = :holderUserId
            """, nativeQuery = true)
    int upsertIfFreeOrSameHolder(
            @Param("patrolId") Long patrolId,
            @Param("holderUserId") Long holderUserId,
            @Param("holderName") String holderName,
            @Param("now") OffsetDateTime now,
            @Param("expiresAt") OffsetDateTime expiresAt
    );

    @Modifying
    @Query("DELETE FROM CustomerEditLock l WHERE l.patrolId = :patrolId AND l.holderUserId = :holderUserId")
    int releaseByHolder(@Param("patrolId") Long patrolId, @Param("holderUserId") Long holderUserId);

    @Modifying
    @Query("DELETE FROM CustomerEditLock l WHERE l.expiresAt < :now")
    int deleteExpired(@Param("now") OffsetDateTime now);

    Optional<CustomerEditLock> findByPatrolId(Long patrolId);
}
