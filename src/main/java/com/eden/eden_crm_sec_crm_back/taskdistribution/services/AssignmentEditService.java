package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentDeltaRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentLookupResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentPatchResponse;

/**
 * US2: per-(service, patrol, period, site) assignment editing. Distinct from
 * the global one-shot distribute path (which lives on {@link TaskDistributionService}).
 */
public interface AssignmentEditService {

    AssignmentLookupResponse get(Long serviceId, Long patrolId, Long serviceTimeId, Long siteId);

    AssignmentPatchResponse applyDelta(AssignmentDeltaRequest request);
}
