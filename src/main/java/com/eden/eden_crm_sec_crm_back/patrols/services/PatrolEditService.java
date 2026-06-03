package com.eden.eden_crm_sec_crm_back.patrols.services;

import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditSaveResponse;

/**
 * US1: edit a Patrol definition (name, frequency, locations, tasks) with
 * copy-on-edit versioning and per-service propagation.
 */
public interface PatrolEditService {

    PatrolEditResponse getForEdit(Long patrolId);

    PatrolEditSaveResponse save(Long patrolId, PatrolEditRequest request);
}
