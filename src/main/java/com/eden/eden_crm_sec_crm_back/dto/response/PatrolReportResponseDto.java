package com.eden.eden_crm_sec_crm_back.dto.response;

import java.util.List;

public class PatrolReportResponseDto {
    private List<PremiseResponseDto> premises;

    public PatrolReportResponseDto() {
    }

    public PatrolReportResponseDto(List<PremiseResponseDto> premises) {
        this.premises = premises;
    }

    public List<PremiseResponseDto> getPremises() {
        return premises;
    }

    public void setPremises(List<PremiseResponseDto> premises) {
        this.premises = premises;
    }
}
