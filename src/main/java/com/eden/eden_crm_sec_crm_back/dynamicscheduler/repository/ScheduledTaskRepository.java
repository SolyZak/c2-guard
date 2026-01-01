package com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, UUID>, JpaSpecificationExecutor<ScheduledTaskEntity> {
    List<ScheduledTaskEntity> findAllByIsActiveTrue();
}
