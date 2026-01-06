package com.eden.eden_crm_sec_crm_back.models.projections;

import com.eden.eden_crm_sec_crm_back.models.Premise;

import java.math.BigDecimal;

public interface LocationProjection {
    Long getId();
    Premise getPremise();
    String getName();
    String getAccessType();
    BigDecimal  getLongitude();
    BigDecimal  getLatitude();
    BigDecimal getTolerance();
}
