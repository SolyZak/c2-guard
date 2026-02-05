package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.OffsetTime;

public interface DistributionTimesProjection {
    Long getId();
    OffsetTime getStartTime();
    OffsetTime getEndTime();
}
