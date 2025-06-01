package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import lombok.Data;

import java.util.List;

@Data
public class ContractServiceDetailsData {
    private Long id;
    private String serviceName;
    private Boolean serviceMultiSite;
    private Long hours;
    private Long days;
    private Long quantity;
    private Long distributedQuantity;
    private Double unitPrice;
    private List<ActivityEnum> serviceActivities;
}
