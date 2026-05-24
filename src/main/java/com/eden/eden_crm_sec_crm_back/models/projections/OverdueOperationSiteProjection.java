package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.OffsetTime;

public interface OverdueOperationSiteProjection {
    Long getOperationSiteId();
    String getPresenceMode();
    Integer getCheckOutAfterMinutes();
    OffsetTime getToTime();
    OffsetTime getEnforcedCheckoutDeadline();
}
