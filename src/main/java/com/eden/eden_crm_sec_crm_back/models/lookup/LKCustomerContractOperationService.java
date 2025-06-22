package com.eden.eden_crm_sec_crm_back.models.lookup;

import com.eden.eden_crm_sec_crm_back.base.model.BaseEntity;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contract_operation_site_distribution_details")
@Setter
@Getter
public class LKCustomerContractOperationService extends BaseEntity<Long> {

    private Long quantity;

    @Enumerated(EnumType.STRING)
    private Set<WeekDaysEnum> days = new HashSet<>();

    private OffsetTime fromTime;
    private OffsetTime toTime;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "contract_operation_site_distribution_id", nullable = false)
    @JsonBackReference
    private SiteDistribution  siteDistribution;

}
