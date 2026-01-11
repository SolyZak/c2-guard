package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CreateScheduledTaskService {
    CompletableFuture<Void> createDistributionScheduledTasks(
        List<ContractOperationSiteDistributionPatrol> distributionForPatrols,
        Long contractId,
        Long serviceId
    );
}
