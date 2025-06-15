package com.eden.eden_crm_sec_crm_back.dto.external;

import com.eden.eden_crm_sec_crm_back.exception.ValidationException;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceStatsDto {
    @NotNull(message = "{validation.attendance-stats.from.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @NotNull(message = "{validation.attendance-stats.to.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    private List<Long> securityCompanyId;

    private List<Long> customerId;

    private List<Long> contractId;

    private List<Long> operationSiteId;

    public void validate() {
        if (to.isBefore(from)) {
            throw new ValidationException("to", MessageUtil.getMessage("to-must-after-from"));
        }

        if (from.plusDays(31).isBefore(to)) {
            throw new ValidationException("to", MessageUtil.getMessage("date-range-max-31-days"));
        }
    }
}
