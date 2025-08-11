package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.entity.ServicePlatform;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ServicePlatformResponse {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private ServicePlatformEnum code;

    public static ServicePlatformResponse of(final ServicePlatform servicePlatform) {
        return ServicePlatformResponse.builder()
                .id(servicePlatform.getId())
                .name(servicePlatform.getName().name())
                .code(ServicePlatformEnum.fromCode(servicePlatform.getCode()))
                .build();
    }
}
