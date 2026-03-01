package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PatrolResponseDetail {
    Long id;
    List<String> locations;
    List<String> tasks;
    List<String> taskDefinitions;

}
