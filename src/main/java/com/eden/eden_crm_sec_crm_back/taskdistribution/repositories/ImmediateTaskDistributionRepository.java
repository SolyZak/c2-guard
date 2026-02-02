package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.ImmediateTaskDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImmediateTaskDistributionRepository extends JpaRepository<ImmediateTaskDistribution, Long>, JpaSpecificationExecutor<ImmediateTaskDistribution> {
}
