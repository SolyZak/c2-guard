package com.eden.eden_crm_sec_crm_back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

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
    private List<PatrolDetail> patrolDetails;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;

}
