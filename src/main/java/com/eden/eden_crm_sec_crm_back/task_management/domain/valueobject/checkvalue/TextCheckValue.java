package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Check value for free-text inputs.
 * JSON: { "type": "TEXT", "notes": "Describe the item condition" }
 */
@JsonTypeName("TEXT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextCheckValue extends TaskCheckValue {

    private String notes;

    @Override
    public boolean isValid() {
        return notes != null && !notes.isBlank();
    }
}
