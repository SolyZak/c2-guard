package com.eden.eden_crm_sec_crm_back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "premise")
public class Premise extends BaseEntity {

    @Id
    @SequenceGenerator(name = "premise_id_seq", sequenceName = "premise_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "premise_id_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "longitude",
            precision = 13,
            scale     = 10,
            nullable  = true, columnDefinition = "DECIMAL(13,10)")
    private BigDecimal longitude;

    @Column(name = "latitude",
            precision = 13,
            scale     = 10,
            nullable  = true, columnDefinition = "DECIMAL(13,10)")
    private BigDecimal latitude;


    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;

}
