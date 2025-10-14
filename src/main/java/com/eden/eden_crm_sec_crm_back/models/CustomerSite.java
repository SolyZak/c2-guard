package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_site")
public class CustomerSite extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "operation_site_seq",
            sequenceName = "operation_site_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_site_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "tolerance")
    private Double tolerance;

    @Column
    private Boolean active ;

    @Column(name = "timezone")
    @Enumerated(EnumType.STRING)
    private CustomTimezone timezone;

    @ManyToOne
    @JoinColumn(name = "premise_id")
    Premise premise;
}
