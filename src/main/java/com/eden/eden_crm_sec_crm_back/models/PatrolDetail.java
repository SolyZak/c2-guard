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

    @Column(name = "task_definition_id")
    private Long taskDefinitionId;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    /**
     * Soft-delete flag. When a task/location is removed from a patrol that still
     * has past/in-progress execution history (which references this row), the
     * row is flagged deleted instead of being physically removed so the history
     * stays intact. Active definition reads filter on {@code deleted = false}.
     */
    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "patrolDetail")
    List<PatrolAssignment> patrolAssignments;

}
