package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkforceTaskCheckPayload {

    // ── Jackson polymorphic discriminator (matches test env's "type" property) ──
    private String type;

    private Long id;
    private String name;
    private Boolean evidence;
    private Boolean commentCheck;
    private String referenceImageUrl;

    // TEXT fields
    private String notes;

    // NUMBER & DECIMAL fields
    private String unit;
    private String operator;
    private Number value;

    // LIST fields
    private List<String> listItems;
}