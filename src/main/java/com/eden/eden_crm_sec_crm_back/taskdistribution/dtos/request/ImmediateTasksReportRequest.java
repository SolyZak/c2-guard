package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ImmediateTasksReportRequest(
    @NotNull LocalDate fromDate,
    @NotNull LocalDate toDate
) {}
