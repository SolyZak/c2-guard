package com.eden.eden_crm_sec_crm_back.dto.external;

import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AttendanceStatsData {
    private Long contractId;
    private String contractName;
    private Long operationSiteId;
    private String operationSiteName;
    private Long securityCompanyId;
    private String securityCompanyName;
    private Long totalAttended;
    private Long totalPlanned;
    private Map<WeekDaysEnum, Long> weekdayPlanned;
}
