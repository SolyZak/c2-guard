package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Check value for decimal numeric inputs with an operator constraint.
 * JSON: { "type": "DECIMAL", "unit": "kg", "operator": "lte", "value": 75.5 }
 */
@JsonTypeName("DECIMAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DecimalCheckValue extends TaskCheckValue {

    private String unit;
    private String operator;
    private Double value;

    @Override
    public boolean isValid() {
        return operator != null && !operator.isBlank() && value != null;
    }
}
