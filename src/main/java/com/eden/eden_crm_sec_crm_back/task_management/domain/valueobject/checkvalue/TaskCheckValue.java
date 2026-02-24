package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract base for polymorphic check-value payloads stored as JSONB.
 * Jackson uses the "type" field in the JSON to select the correct subclass at runtime.
 *
 * To add a new check type:
 *  1. Create a concrete subclass of TaskCheckValue.
 *  2. Annotate it with @JsonTypeName("YOUR_TYPE").
 *  3. Register it in @JsonSubTypes below — no other changes required.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextCheckValue.class, name = "TEXT"),
        @JsonSubTypes.Type(value = NumberCheckValue.class, name = "NUMBER"),
        @JsonSubTypes.Type(value = DecimalCheckValue.class, name = "DECIMAL"),
        @JsonSubTypes.Type(value = ListCheckValue.class, name = "LIST")
})
public abstract class TaskCheckValue {

    /**
     * Strategy method — each subtype validates its own fields.
     * Callers work only against this interface; no instanceof or switch required.
     */
    public abstract boolean isValid();
}
