package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendors")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vendor {
    @Id
    @SequenceGenerator(name = "vendors_seq", sequenceName = "vendors_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vendors_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}

