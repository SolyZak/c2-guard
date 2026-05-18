package com.eden.eden_crm_sec_crm_back.taskdistribution.events;

import java.util.Set;

public record ImmediateTaskAssignedEvent(
    Set<Long> workforceIds,
    String locationName,
    Long immediateTaskDistributionId
) {}
