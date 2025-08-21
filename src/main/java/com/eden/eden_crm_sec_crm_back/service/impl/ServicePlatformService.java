package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.response.ServicePlatformResponse;
import com.eden.eden_crm_sec_crm_back.entity.ServicePlatform;
import com.eden.eden_crm_sec_crm_back.repository.ServicePlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePlatformService {
    private final ServicePlatformRepository servicePlatformRepository;

    public ServicePlatform getServicePlatformById(final Long servicePlatformId) {
        return servicePlatformRepository.findById(servicePlatformId)
                .orElseThrow(() -> new RuntimeException("ServicePlatform not found"));
    }

}
