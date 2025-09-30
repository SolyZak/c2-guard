package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Premise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PremiseRepository extends JpaRepository<Premise, Long>, JpaSpecificationExecutor<Premise> {

    Optional<Premise> findByCodeOrName(String code, String name);
}
