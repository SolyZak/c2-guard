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
 * One row per save on the US1 patrol-edit endpoint. Records the copy-on-edit
 * (previous version -> new version) and the list of services whose schedules
 * were regenerated as a side effect.
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

    @Column(name = "previous_patrol_id", nullable = false)
    private Long previousPatrolId;

    @Column(name = "new_patrol_id", nullable = false)
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
}
