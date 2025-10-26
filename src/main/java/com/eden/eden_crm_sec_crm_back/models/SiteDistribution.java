package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract_operation_site_distribution")
public class SiteDistribution extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_site_id")
    @JsonBackReference
    private CustomerSite site;

    @ElementCollection(targetClass = ActivityEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "contract_operation_site_distribution_activities",
            joinColumns = @JoinColumn(name = "operation_site_distribution_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_operation_site_distribution_id") // ensure this points to SiteDistribution
    )
    private Set<ActivityEnum> activities = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_contract_service_id", nullable = false)
    @JsonBackReference
    private LKCustomerContractService lkCustomerContractService;

    @OneToMany(mappedBy = "siteDistribution", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LKCustomerContractOperationService> operationServices = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_contract_id", nullable = false)
    @JsonBackReference
    private CustomerContract customerContract;

    @OneToOne
    @JoinColumn(name = "contract_operation_site_distribution_patrol_id", referencedColumnName = "id")
    ContractOperationSiteDistributionPatrol contractOperationSiteDistributionPatrol;
}
