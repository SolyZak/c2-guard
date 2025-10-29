package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ContractDistributionForPatrol {
    private Long patrolId;
    private Long siteId;
    private LocalDate startDate;
    private List<LocationsTasksForPatrol> locations;
}
