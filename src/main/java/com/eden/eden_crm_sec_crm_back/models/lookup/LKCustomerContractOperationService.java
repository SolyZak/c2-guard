package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_contract_operation_service")
@Setter
@Getter
public class LKCustomerContractOperationService extends BaseEntity<Long> {

    private Long quantity;
    @Enumerated(EnumType.STRING)
    private Set<WeekDaysEnum> days = new HashSet<>();
    private LocalTime fromTime;
    private LocalTime toTime;
    @ManyToOne
    @JoinColumn(name = "contract_site_distribution_id", nullable = false)
    private SiteDistribution  siteDistribution;
    @ManyToOne
    @JoinColumn(name = "work_site_distribution_location_id", nullable = false)
    private WorkSiteDistributionLocation  workSiteDistributionLocation;

}
