package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;

import java.time.OffsetTime;

@Data
public class TimeDto {
    OffsetTime fromTime;
    OffsetTime toTime;
}
