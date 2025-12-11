package com.eden.eden_crm_sec_crm_back.dto.response;

import java.util.List;

public class PatrolReportResponseDto {
    private Object patrolsByPremise; // using Object to match requested dynamic map-like structure
    private List<PremiseResponseDto> premises;

    public PatrolReportResponseDto() {
    }

    public PatrolReportResponseDto(Object patrolsByPremise, List<PremiseResponseDto> premises) {
        this.patrolsByPremise = patrolsByPremise;
        this.premises = premises;
    }

    public Object getPatrolsByPremise() {
        return patrolsByPremise;
    }

    public void setPatrolsByPremise(Object patrolsByPremise) {
        this.patrolsByPremise = patrolsByPremise;
    }

    public List<PremiseResponseDto> getPremises() {
        return premises;
    }

    public void setPremises(List<PremiseResponseDto> premises) {
        this.premises = premises;
    }
}

