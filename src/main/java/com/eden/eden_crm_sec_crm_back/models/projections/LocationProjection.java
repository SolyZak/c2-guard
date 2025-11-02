package com.eden.eden_crm_sec_crm_back.models.projections;

import com.eden.eden_crm_sec_crm_back.models.Premise;

public interface LocationProjection {
    Long getId();
    Premise getPremise();
    String getName();
    String getAccessType();
    Double getLongitude();
    Double getLatitude();
}
