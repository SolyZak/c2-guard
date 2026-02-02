package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TaskExecutionSlotRepository extends JpaRepository<TaskExecutionSlot, Long>, JpaSpecificationExecutor<TaskExecutionSlot> {
    List<TaskExecutionSlot> findByStartDateTimeBetween(OffsetDateTime start, OffsetDateTime end);
}
