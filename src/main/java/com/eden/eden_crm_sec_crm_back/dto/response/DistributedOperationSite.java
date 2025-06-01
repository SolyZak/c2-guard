package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class DistributedOperationSite {
    private Long operationSiteId;
    private String operationSiteName;
    private Long quantity;
    private Set<ActivityEnum> activities;
    private List<DistributedOperationSiteDetail> details;
}
