package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
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
@Table(name = "site_distribution")
public class SiteDistribution extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_site_id")
    private CustomerSite site;

    @ElementCollection(targetClass = ActivityEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "site_distribution_activities",
            joinColumns = @JoinColumn(name = "site_distribution_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_site_distribution_id") // ensure this points to SiteDistribution
    )
    private Set<ActivityEnum> activities = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_service_id", nullable = false)
    private LKCustomerContractService lkCustomerContractService;

    @OneToMany(mappedBy = "siteDistribution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LKCustomerContractOperationService> operationServices = new ArrayList<>();
}
