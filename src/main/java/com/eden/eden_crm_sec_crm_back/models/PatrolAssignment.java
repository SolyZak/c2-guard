package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patrol_assignment")
public class PatrolAssignment {
    @Id
    @SequenceGenerator(name = "patrol_assignment_seq",
            sequenceName = "patrol_assignment_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patrol_assignment_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patrol_detail_id", nullable = false)
    PatrolDetail patrolDetail;
}
