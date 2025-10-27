package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<Long, List<Long>> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_service_id", nullable = false)
    private CustomerService customerService;

    @ManyToOne
    @JoinColumn(name = "customer_contract_id", nullable = false)
    private CustomerContract customerContract;
}
