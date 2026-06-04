package com.eden.eden_crm_sec_crm_back.patrols.services;

import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditSaveResponse;

/**
 * US1: edit a Patrol definition (name, frequency, locations, tasks) in place.
 * The patrol is mutated directly (no versioning); future task slots are
 * regenerated for every service using the patrol, and the edit is recorded in
 * patrol_version_audit.
 */
public interface PatrolEditService {

    PatrolEditResponse getForEdit(Long patrolId);

    PatrolEditSaveResponse save(Long patrolId, PatrolEditRequest request);
}
