package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPatrolDetailRequest {
    List<Long> locations;

    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    // Legacy field: IDs pointing to the old `task` table.
    // CLEANUP: remove after Phase E migration.
    List<Long> tasks;
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // New field: IDs pointing to task_definition (task_management module).
    // CLEANUP: rename this to `tasks` after Phase E and add @NotEmpty validation.
    List<Long> taskDefinitionIds;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
}
