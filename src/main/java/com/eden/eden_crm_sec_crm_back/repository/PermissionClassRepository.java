package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PermissionClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionClassRepository extends JpaRepository<PermissionClassEntity, Integer> {
    List<PermissionClassEntity> findByDeletedFalseOrderByIdAsc();
}