package com.eden.eden_crm_sec_crm_back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "complaint")
@Getter
@Setter
public class ComplaintEntity extends BaseEntity {
    @Id
    @SequenceGenerator(name = "complaint_seq", sequenceName = "complaint_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "complaint_seq")
    private Integer id;

    @Column(name = "description", length = 1500)
    private String description;

    @Column(name = "evidences_paths")
    private List<String> evidencesPaths;

    @ManyToOne
    @JoinColumn(name = "customer_site_id")
    @JsonBackReference
    private CustomerSite customerSite;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;
}
