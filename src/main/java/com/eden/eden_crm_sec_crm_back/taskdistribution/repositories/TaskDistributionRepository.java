package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDistributionRepository extends JpaRepository<TaskDistribution, Long>, JpaSpecificationExecutor<TaskDistribution> {
}
