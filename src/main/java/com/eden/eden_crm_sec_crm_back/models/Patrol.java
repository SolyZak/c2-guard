package com.eden.eden_crm_sec_crm_back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patrol")
public class Patrol extends BaseEntity {
    @Id
    @SequenceGenerator(name = "patrol_seq",
            sequenceName = "patrol_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patrol_seq")
    private Long id;
    private String name;
    private String frequency;
    private String frequencyRate;

    @OneToMany(mappedBy = "patrol", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<PatrolDetail> patrolDetails;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;

    /** Inclusive start date of this Patrol version. */
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    /** Inclusive end date of this Patrol version; {@code null} means current. */
    @Column(name = "valid_to")
    private LocalDate validTo;

    /** Self-reference to the prior Patrol version this one supersedes. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_patrol_id")
    private Patrol previousPatrol;
}
