package com.eden.eden_crm_sec_crm_back.enums;

import lombok.Getter;

/**
 * This enum is used to define the trigger code
 * so once the trigger code is added to the database
 * we can use it to get the trigger
 * its mandatory to add here all the new triggers add from the database
 * the trigger code is used to identify the trigger
 * and to get the trigger from the database
 */
@Getter
public enum TriggerCode {
    PATROL_TASK_MISSED(1L),
    // Fired when a submitted check value violates its defined rule (list alertValue, number/decimal threshold)
    PATROL_TASK_DEVIATION(2L);   // DB code: PATROL_TASK_Deviation

    private final Long id;

    TriggerCode(Long id) {
        this.id = id;
    }

}
