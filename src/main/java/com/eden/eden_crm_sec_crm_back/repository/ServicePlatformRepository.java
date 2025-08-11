package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.entity.ServicePlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePlatformRepository extends JpaRepository<ServicePlatform, Long> {
}