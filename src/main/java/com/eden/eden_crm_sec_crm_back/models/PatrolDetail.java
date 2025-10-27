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

    @ManyToMany(mappedBy = "patrolDetails")
    private List<Location> locations;

    @ManyToMany(mappedBy = "patrolDetails")
    List<Task> tasks;

}
