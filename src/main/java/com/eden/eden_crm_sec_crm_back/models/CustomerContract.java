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
@Table(
        name = "customer_contract",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customer_contract_agreement_number_per_customer",
                        columnNames = {"agreement_number", "customer_id"}
                ),
                @UniqueConstraint(
                        name = "uk_customer_contract_cloud_contract_id",
                        columnNames = {"cloud_contract_id"}
                )
        },
        indexes = {
                @Index(name = "idx_customer_contract_cloud_contract_id", columnList = "cloud_contract_id")
        }
)
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

    // ── Cloud federation link fields ──────────────────────────────────────
    @Column(name = "cloud_contract_id")
    private Long cloudContractId;

    @Column(name = "cloud_customer_id")
    private Long cloudCustomerId;

    @Column(name = "global_customer_uuid")
    private UUID globalCustomerUuid;

    @Column(name = "sub_cloud_account_id")
    private Long subCloudAccountId;

    @ElementCollection
    @CollectionTable(
            name = "customer_contract_cloud_module",
            joinColumns = @JoinColumn(name = "customer_contract_id")
    )
    @Column(name = "module_code")
    private Set<String> cloudModuleCodes = new HashSet<>();
}
