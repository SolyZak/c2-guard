package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.OffsetTime;

public interface DistributionTimesProjection {
    OffsetTime getStartTime();
    OffsetTime getEndTime();
}
