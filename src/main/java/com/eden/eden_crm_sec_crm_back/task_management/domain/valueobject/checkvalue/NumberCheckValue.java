package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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

    private static final Set<String> VALID_OPERATORS = Set.of("gte", "lte", "gt", "lt", "eq", "ne");

    private String unit;
    @Setter(AccessLevel.NONE)
    private String operator;
    private Integer value;

    public void setOperator(String operator) {
        this.operator = normalizeOperator(operator);
    }

    @Override
    public boolean isValid() {
        return operator != null && VALID_OPERATORS.contains(operator) && value != null;
    }
}
