package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPatrolDetailRequest {
    List<Long> locations;

    @NotEmpty
    List<Long> taskDefinitionIds;
}
