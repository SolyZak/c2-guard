package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PatrolTaskDistributionRepository extends JpaRepository<PatrolTaskDistribution, Long>, JpaSpecificationExecutor<PatrolTaskDistribution> {
    boolean existsByServiceTime_IdAndPatrolDetail_Id(Long serviceTimeId, Long patrolDetailId);
}
