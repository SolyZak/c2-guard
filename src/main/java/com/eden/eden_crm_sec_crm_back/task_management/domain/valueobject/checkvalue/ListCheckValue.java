package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Check value for predefined option-list inputs.
 * JSON: { "type": "LIST", "items": ["Pass", "Fail", "N/A"] }
 */
@JsonTypeName("LIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListCheckValue extends TaskCheckValue {

    private List<String> items;

    @Override
    public boolean isValid() {
        return items != null && !items.isEmpty();
    }
}
