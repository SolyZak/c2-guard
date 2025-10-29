package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class LocationsTasksForPatrol {
    private Long locationId;
    private List<Long> tasks;
}
