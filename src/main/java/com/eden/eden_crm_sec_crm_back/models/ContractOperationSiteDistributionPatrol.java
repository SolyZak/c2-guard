package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.config.MapJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Convert(converter = MapJsonConverter.class)
    Map<Long, List<Long>> locations;

    @OneToOne(mappedBy = "contractOperationSiteDistributionPatrol")
    private SiteDistribution siteDistribution;
}
