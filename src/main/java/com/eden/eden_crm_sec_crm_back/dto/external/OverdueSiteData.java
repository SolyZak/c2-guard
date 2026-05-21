package com.eden.eden_crm_sec_crm_back.dto.external;

import java.time.OffsetTime;

public record OverdueSiteData(
        Long operationSiteId,
        String presenceMode,
        Integer checkOutAfterMinutes,
        OffsetTime toTime,
        OffsetTime enforcedCheckoutDeadline
) {}
