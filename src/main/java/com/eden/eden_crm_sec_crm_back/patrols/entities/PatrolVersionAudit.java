package com.eden.eden_crm_sec_crm_back.patrols.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * One row per save on the US1 patrol-edit endpoint. The patrol is edited in
 * place (no versioning), so each row records which patrol was edited, the
 * before/after definition snapshots, the field-level {@code changes} delta, and
 * the list of services whose schedules were regenerated as a side effect.
 *
 * <p>{@code previousPatrolId}/{@code newPatrolId} are retained (nullable) only
 * for legacy copy-on-edit rows written before the in-place migration.
 */
@Entity
@Table(name = "patrol_version_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatrolVersionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "edit_session_id", nullable = false)
    private UUID editSessionId;

    /** The patrol that was edited (in place). */
    @Column(name = "patrol_id")
    private Long patrolId;

    /** Legacy copy-on-edit columns; null for in-place edits. */
    @Column(name = "previous_patrol_id")
    private Long previousPatrolId;

    @Column(name = "new_patrol_id")
    private Long newPatrolId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "actor_user_id", nullable = false)
    private Long actorUserId;

    @Column(name = "actor_user_name", nullable = false)
    private String actorUserName;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "cutoff_date", nullable = false)
    private LocalDate cutoffDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_snapshot", nullable = false, columnDefinition = "jsonb")
    private String beforeSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_snapshot", nullable = false, columnDefinition = "jsonb")
    private String afterSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "affected_services", nullable = false, columnDefinition = "jsonb")
    private String affectedServices;

    /** Human-readable field-level delta of the edit (name/frequency/tasks/locations). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changes", nullable = false, columnDefinition = "jsonb")
    private String changes;
}
