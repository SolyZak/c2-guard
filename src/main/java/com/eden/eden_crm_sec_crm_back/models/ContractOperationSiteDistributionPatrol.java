package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetTime;

@Entity
@Table(name = "contract_operation_distribution_site_patrol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractOperationSiteDistributionPatrol {
    @Id
    @SequenceGenerator(name = "contract_operation_site_distribution_patrol_seq",
            sequenceName = "contract_operation_site_distribution_patrol_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_operation_site_distribution_patrol_seq")
    private Long id;

    Long patrolId;

    Long siteId;

    LocalDate startDate;
    LocalDate endDate;
    private Long locationId;
    private Long taskId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_contract_service_id", nullable = false)
    private LKCustomerContractService customerService;

    @ManyToOne
    @JoinColumn(name = "customer_contract_id", nullable = false)
    private CustomerContract customerContract;

    private OffsetTime fromTime;
    private OffsetTime toTime;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    private String patrolFrequencyType;
    private String status;
    private String uniqueId;
}
