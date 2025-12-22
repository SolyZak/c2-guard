package com.eden.eden_crm_sec_crm_back.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location")
public class Location extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "location_seq",
            sequenceName = "location_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "premise_id", referencedColumnName = "id")
    private Premise premise;
    private String name;
    private String accessType;

    @Column(name = "longitude",
            precision = 13,
            scale     = 10,
            nullable  = true,
            columnDefinition = "DECIMAL(13,10)")
    private BigDecimal longitude;

    @Column(name = "latitude",
            precision = 13,
            scale     = 10,
            nullable  = true,
            columnDefinition = "DECIMAL(13,10)")
    private BigDecimal latitude;

    @Column(name = "tolerance",
            precision = 10,
            scale     = 2,
            nullable  = true,
            columnDefinition = "NUMERIC(10,2)")
    private BigDecimal tolerance;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] qrImage;

    @OneToMany(mappedBy = "location")
    List<PatrolDetail> patrolDetails;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;
}
