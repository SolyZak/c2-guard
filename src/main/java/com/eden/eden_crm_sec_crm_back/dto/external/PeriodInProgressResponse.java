package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodInProgressResponse {
    private boolean inProgress;
    private List<Long> checkedInWorkforceIds;
}
