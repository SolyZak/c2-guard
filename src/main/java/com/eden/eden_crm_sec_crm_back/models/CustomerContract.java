package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "customer_contract",uniqueConstraints = {@UniqueConstraint(columnNames = {"agreementNumber"})})
@Setter
@Getter
public class CustomerContract extends BaseEntity<Long> {
    @Column(name = "agreement_number")
    private String agreementNumber;

    @Column(name = "agreement_name")
    private String agreementName;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private LocalDate startAgreementDate;
    private LocalDate endAgreementDate;
    private Long securityCompanyId;
    private String securityCompanyName;

    private Long currency;
    private String currencyName;
    private String currencyCode;

    @OneToOne(mappedBy = "customerAgreement", fetch = FetchType.EAGER)
    private ContractOperationRule customerAgreement;

    @OneToMany(mappedBy = "customerContract", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonManagedReference
    private List<LKCustomerContractService> customerContractServices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @OneToMany(mappedBy = "customerContract", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonManagedReference
    private Set<SiteDistribution> siteDistributions = new HashSet<>();
}
