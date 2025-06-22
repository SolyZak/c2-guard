package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailsData {
    private Long id;
    private String agreementNumber;
    private String agreementName;
    private LocalDate startAgreementDate;
    private LocalDate endAgreementDate;
    private ContractStatus status;
    private String currencyCode;
    private String currencyName;
    private Integer checkInBeforeMinutes;
    private Integer checkInAfterMinutes;
    private Integer checkOutBeforeMinutes;
    private PresenceMode presenceMode;
    private List<ContractServiceDetails> services;

    @Data
    @Builder
    public static class ContractServiceDetails {
        private String name;
        private Long hours;
        private Long days;
        private Long quantity;
        private Double unitPrice;
        private List<ContractServiceDistributionsData> distributions;

        @Data
        @Builder
        public static class ContractServiceDistributionsData {
            private String operationSiteName;
            private Long quantity;
            private Set<ActivityEnum> activities;
            private List<ContractOperationServiceDetails> operationServices;

            @Data
            @Builder
            public static class ContractOperationServiceDetails {
                private Long quantity;
                private Set<WeekDaysEnum> days;
                private OffsetTime fromTime;
                private OffsetTime toTime;
            }
        }
    }
}
