package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

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
 * One row per save on the US2 assignment-edit endpoint. Holds full before/after
 * snapshots so a single row tells the complete state of an assignment at a
 * point in time, plus a delta record for easy "what changed".
 */
@Entity
@Table(name = "patrol_assignment_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatrolAssignmentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "edit_session_id", nullable = false)
    private UUID editSessionId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "patrol_id", nullable = false)
    private Long patrolId;

    @Column(name = "service_time_id", nullable = false)
    private Long serviceTimeId;

    @Column(name = "site_id", nullable = false)
    private Long siteId;

    @Column(name = "actor_user_id", nullable = false)
    private Long actorUserId;

    @Column(name = "actor_user_name", nullable = false)
    private String actorUserName;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "cutoff_date", nullable = false)
    private LocalDate cutoffDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tasks_before", nullable = false, columnDefinition = "jsonb")
    private String tasksBefore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tasks_after", nullable = false, columnDefinition = "jsonb")
    private String tasksAfter;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "delta", nullable = false, columnDefinition = "jsonb")
    private String delta;
}
