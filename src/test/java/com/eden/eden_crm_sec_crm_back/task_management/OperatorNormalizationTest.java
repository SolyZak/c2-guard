package com.eden.eden_crm_sec_crm_back.task_management;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests operator alias normalization in NumberCheckValue and DecimalCheckValue,
 * and isValid() validation for both types.
 */
class OperatorNormalizationTest {

    // ─── NumberCheckValue — alias mapping via setOperator() ──────────────────────
    // Note: @AllArgsConstructor assigns the field directly (no normalization).
    // Normalization only happens through the custom setOperator() method.

    @Test
    void numberCheckValue_setOperator_less_normalizesToLt() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("less");
        assertThat(nv.getOperator()).isEqualTo("lt");
    }

    @Test
    void numberCheckValue_setOperator_greater_normalizesToGt() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("greater");
        assertThat(nv.getOperator()).isEqualTo("gt");
    }

    @Test
    void numberCheckValue_setOperator_lessThanOrEqual_normalizesToLte() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("less_than_or_equal");
        assertThat(nv.getOperator()).isEqualTo("lte");
    }

    @Test
    void numberCheckValue_setOperator_lteEqual_normalizesToLte() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("lte_equal");
        assertThat(nv.getOperator()).isEqualTo("lte");
    }

    @Test
    void numberCheckValue_setOperator_greaterThanOrEqual_normalizesToGte() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("greater_than_or_equal");
        assertThat(nv.getOperator()).isEqualTo("gte");
    }

    @Test
    void numberCheckValue_setOperator_equal_normalizesToEq() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("equal");
        assertThat(nv.getOperator()).isEqualTo("eq");
    }

    @Test
    void numberCheckValue_setOperator_notEqual_normalizesToNe() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("not_equal");
        assertThat(nv.getOperator()).isEqualTo("ne");
    }

    @Test
    void numberCheckValue_setOperator_canonicalPassesThrough() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("gte");
        assertThat(nv.getOperator()).isEqualTo("gte");
    }

    // ─── NumberCheckValue — isValid() ────────────────────────────────────────────

    @Test
    void numberCheckValue_isValid_trueForCanonicalOperatorAndValue() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("gte");
        nv.setValue(10);
        assertThat(nv.isValid()).isTrue();
    }

    @Test
    void numberCheckValue_isValid_falseWhenOperatorNull() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setValue(10);
        assertThat(nv.isValid()).isFalse();
    }

    @Test
    void numberCheckValue_isValid_falseWhenValueNull() {
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("gte");
        assertThat(nv.isValid()).isFalse();
    }

    @Test
    void numberCheckValue_isValid_falseForUnknownOperator() {
        // "unknown_op" is not an alias and not in VALID_OPERATORS — stays as-is → invalid
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("unknown_op");
        nv.setValue(10);
        assertThat(nv.isValid()).isFalse();
    }

    @Test
    void numberCheckValue_isValid_trueAfterAliasNormalization() {
        // "less" → "lt" (in VALID_OPERATORS) → valid
        NumberCheckValue nv = new NumberCheckValue();
        nv.setOperator("less");
        nv.setValue(10);
        assertThat(nv.isValid()).isTrue();
    }

    // ─── DecimalCheckValue — alias mapping via setOperator() ─────────────────────

    @Test
    void decimalCheckValue_setOperator_less_normalizesToLt() {
        DecimalCheckValue dv = new DecimalCheckValue();
        dv.setOperator("less");
        assertThat(dv.getOperator()).isEqualTo("lt");
    }

    @Test
    void decimalCheckValue_setOperator_greaterThanOrEqual_normalizesToGte() {
        DecimalCheckValue dv = new DecimalCheckValue();
        dv.setOperator("greater_than_or_equal");
        assertThat(dv.getOperator()).isEqualTo("gte");
    }

    @Test
    void decimalCheckValue_setOperator_canonicalPassesThrough() {
        DecimalCheckValue dv = new DecimalCheckValue();
        dv.setOperator("lte");
        assertThat(dv.getOperator()).isEqualTo("lte");
    }

    // ─── DecimalCheckValue — isValid() ───────────────────────────────────────────

    @Test
    void decimalCheckValue_isValid_trueForCanonicalOperatorAndValue() {
        DecimalCheckValue dv = new DecimalCheckValue();
        dv.setOperator("lt");
        dv.setValue(75.5);
        assertThat(dv.isValid()).isTrue();
    }

    @Test
    void decimalCheckValue_isValid_falseWhenOperatorNull() {
        DecimalCheckValue dv = new DecimalCheckValue();
        dv.setValue(75.5);
        assertThat(dv.isValid()).isFalse();
    }

    @Test
    void decimalCheckValue_isValid_falseWhenValueNull() {
        DecimalCheckValue dv = new DecimalCheckValue();
        dv.setOperator("lte");
        assertThat(dv.isValid()).isFalse();
    }
}
