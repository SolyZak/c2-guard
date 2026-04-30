package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Check value for predefined option-list inputs.
 * JSON: { "type": "LIST", "items": ["Pass", "Fail", "N/A"], "alertValue": "Fail" }
 */
@JsonTypeName("LIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCheckValue extends TaskCheckValue {

    private List<String> items;
    private String alertValue;   // optional; if set, must be one of items

    @Override
    public boolean isValid() {
        if (items == null || items.isEmpty()) return false;
        if (alertValue != null && !items.contains(alertValue)) return false;
        return true;
    }
}
