package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Check value for integer numeric inputs with an operator constraint.
 * JSON: { "type": "NUMBER", "unit": "items", "operator": "gte", "value": 10 }
 */
@JsonTypeName("NUMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NumberCheckValue extends TaskCheckValue {

    private String unit;
    private String operator;
    private Integer value;

    @Override
    public boolean isValid() {
        return operator != null && !operator.isBlank() && value != null;
    }
}
