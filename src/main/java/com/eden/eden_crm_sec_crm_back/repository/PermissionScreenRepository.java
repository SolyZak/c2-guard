package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PermissionScreenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionScreenRepository extends JpaRepository<PermissionScreenEntity, Integer> {
    List<PermissionScreenEntity> findByDeletedFalseOrderByIdAsc();
}