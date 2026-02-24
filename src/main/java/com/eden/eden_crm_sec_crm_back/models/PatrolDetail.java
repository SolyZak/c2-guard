package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patrol_detail")
public class PatrolDetail {
    @Id
    @SequenceGenerator(name = "patrol_detail_seq",
            sequenceName = "patrol_detail_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patrol_detail_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patrol_id", nullable = false)
    Patrol patrol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    // nullable = true: new patrol details created via taskDefinitionId path leave this null.
    // CLEANUP: drop @ManyToOne task field entirely after Phase E migration.
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = true)
    Task task;
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Plain Long — no @ManyToOne to task_management JPA entities (cross-module isolation).
    // CLEANUP: remove nullable = true after Phase E (make column NOT NULL).
    @Column(name = "task_definition_id", nullable = true)
    private Long taskDefinitionId;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @OneToMany(mappedBy = "patrolDetail")
    List<PatrolAssignment> patrolAssignments;

}
